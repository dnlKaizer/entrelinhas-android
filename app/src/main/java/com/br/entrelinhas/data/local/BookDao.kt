package com.br.entrelinhas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * DAO do Room para a entidade Book.
 * Todas as operações são suspend functions, compatíveis com Coroutines.
 */
@Dao
interface BookDao {

    /** Retorna todos os livros em cache, ordenados por nome. */
    @Query("SELECT * FROM books ORDER BY nome ASC")
    suspend fun getAllBooks(): List<BookEntity>

    /** Retorna um livro pelo ID, ou null se não estiver em cache. */
    @Query("SELECT * FROM books WHERE idLivro = :id LIMIT 1")
    suspend fun getBookById(id: Int): BookEntity?

    /**
     * Insere ou substitui uma lista de livros.
     * REPLACE apaga o registro antigo e insere o novo — garante consistência total.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<BookEntity>)

    /** Insere ou substitui um único livro (usado após cadastro remoto). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    /** Apaga todo o cache local. Chamado antes de persistir dados frescos do Supabase. */
    @Query("DELETE FROM books")
    suspend fun deleteAll()

    /**
     * Retorna o timestamp do cache mais recente (MAX de cachedAt).
     * Usado pelo repositório para decidir se o cache ainda é válido.
     * Retorna null se a tabela estiver vazia.
     */
    @Query("SELECT MAX(cachedAt) FROM books")
    suspend fun getLastCachedAt(): Long?
}