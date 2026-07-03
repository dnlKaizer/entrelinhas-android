package com.br.entrelinhas.data.remote

import com.br.entrelinhas.data.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

/**
 * Fonte de dados remota responsável por toda comunicação com o Supabase (tabela Livro).
 * A UI nunca acessa esta classe diretamente — acessa apenas via BookRepository.
 */
class BookRemoteDataSource(private val client: SupabaseClient) {

    /** Retorna todos os livros do banco (sem filtro de usuário pois não há autenticação). */
    suspend fun getBooks(): List<BookDto> {
        SupabaseClientProvider.ensureLoggedIn()
        return client.from(SupabaseConfig.TABLE_LIVRO)
            .select()
            .decodeList<BookDto>()
    }

    /** Retorna um livro pelo ID, ou null se não encontrado. */
    suspend fun getBookById(id: Int): BookDto? {
        SupabaseClientProvider.ensureLoggedIn()
        return client.from(SupabaseConfig.TABLE_LIVRO)
            .select {
                filter { eq("idLivro", id) }
            }
            .decodeList<BookDto>()
            .firstOrNull()
    }

    /**
     * Insere um novo livro no banco e retorna o registro criado (com idLivro gerado).
     * O campo [dto.idUsuario] é obrigatório mesmo sem autenticação — usa o admin user ID.
     */
    suspend fun insertBook(dto: BookInsertDto): BookDto {
        SupabaseClientProvider.ensureLoggedIn()
        return client.from(SupabaseConfig.TABLE_LIVRO)
            .insert(dto) { select() }
            .decodeSingle<BookDto>()
    }
}