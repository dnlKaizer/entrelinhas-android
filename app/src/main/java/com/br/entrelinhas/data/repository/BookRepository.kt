package com.br.entrelinhas.data.repository

import com.br.entrelinhas.data.config.SupabaseConfig
import com.br.entrelinhas.data.local.BookDao
import com.br.entrelinhas.data.local.BookMapper.toDomain
import com.br.entrelinhas.data.local.BookMapper.toEntity
import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.remote.BookRemoteDataSource
import com.br.entrelinhas.data.remote.BookInsertDto

/**
 * Repositório central de livros. É a única porta de entrada para dados de livros na UI.
 *
 * Responsabilidades:
 *  - Encapsular acesso remoto (Supabase) e local (Room)
 *  - Implementar a estratégia de cache: "Cache-First com background refresh"
 *  - Garantir que a UI nunca acesse Supabase diretamente
 *
 * Estratégia de cache (getBooks):
 *  1. Verificar se existe cache válido (< [CACHE_VALIDITY_MS]).
 *  2. Se válido → retornar cache imediatamente (rápido para a UI).
 *     → chamador (ViewModel) dispara refresh em background.
 *  3. Se inválido/vazio → buscar do Supabase, persistir e retornar.
 *  4. Se Supabase falhar mas há cache (expirado) → retornar cache como fallback.
 */
class BookRepository(
    private val remoteDataSource: BookRemoteDataSource,
    private val bookDao: BookDao
) {
    companion object {
        /** Cache válido por 30 minutos. */
        private const val CACHE_VALIDITY_MS = 30 * 60 * 1_000L
    }

    /**
     * Retorna livros aplicando a estratégia de cache descrita acima.
     * Retorna [Pair.first] = lista de livros e [Pair.second] = true se veio do cache
     * (para que o ViewModel saiba se deve disparar um refresh em background).
     */
    suspend fun getBooks(): Pair<List<Book>, Boolean> {
        val cachedEntities = bookDao.getAllBooks()
        val lastCachedAt = bookDao.getLastCachedAt() ?: 0L
        val cacheAge = System.currentTimeMillis() - lastCachedAt
        val cacheIsValid = cachedEntities.isNotEmpty() && cacheAge < CACHE_VALIDITY_MS

        if (cacheIsValid) {
            // Cache válido: retorna imediatamente, ViewModel fará refresh em background
            return Pair(cachedEntities.map { it.toDomain() }, true)
        }

        // Cache inválido ou vazio: busca do Supabase
        return try {
            val fresh = fetchAndCache()
            Pair(fresh, false)
        } catch (e: Exception) {
            if (cachedEntities.isNotEmpty()) {
                // Fallback: retorna cache expirado se rede falhar
                Pair(cachedEntities.map { it.toDomain() }, false)
            } else {
                throw e
            }
        }
    }

    /**
     * Força atualização dos dados do Supabase, persiste no cache e retorna a lista fresca.
     * Chamado pelo ViewModel em background quando [getBooks] retornou dados do cache.
     */
    suspend fun refreshBooks(): List<Book> = fetchAndCache()

    /**
     * Retorna um único livro pelo ID.
     * Consulta o cache local primeiro; se não encontrar, busca no Supabase.
     */
    suspend fun getBookById(id: Int): Book? {
        val cached = bookDao.getBookById(id)
        if (cached != null) return cached.toDomain()

        return try {
            remoteDataSource.getBookById(id)?.toDomain()
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Cria um novo livro no Supabase e persiste o resultado no cache local.
     * [imageUrl] já deve ser a URL pública retornada pelo StorageService após o upload.
     */
    suspend fun createBook(
        nome: String,
        autor: String?,
        numPag: Int,
        status: String,
        ano: Int?,
        text: String?,
        imageUrl: String?
    ): Book {
        val dto = BookInsertDto(
            idUsuario  = SupabaseConfig.ADMIN_KEY,
            nome       = nome,
            numPag     = numPag,
            numPagRead = 0,
            status     = status,
            autor      = autor,
            ano        = ano,
            text       = text,
            img        = imageUrl
        )
        val created = remoteDataSource.insertBook(dto)
        bookDao.insert(created.toEntity())
        return created.toDomain()
    }

    // region Helpers privados

    private suspend fun fetchAndCache(): List<Book> {
        val dtos = remoteDataSource.getBooks()
        val now = System.currentTimeMillis()
        bookDao.deleteAll()
        bookDao.insertAll(dtos.map { it.toEntity(now) })
        return dtos.map { it.toDomain() }
    }

    // endregion
}