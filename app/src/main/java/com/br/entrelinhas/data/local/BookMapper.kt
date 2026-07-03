package com.br.entrelinhas.data.local

import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.model.BookStatus
import com.br.entrelinhas.data.remote.BookDto

/**
 * Mappers de conversão entre as três representações de livro:
 *  - BookDto      → dados vindos do Supabase (rede)
 *  - BookEntity   → dados armazenados no Room (cache local)
 *  - Book         → modelo de domínio usado pela UI
 *
 * O sentido do fluxo é sempre:
 *   BookDto → Book       (apresentar na UI)
 *   BookDto → BookEntity (persistir no cache)
 *   BookEntity → Book    (ler do cache e apresentar na UI)
 *   Book → BookEntity    (atualizar cache após criação)
 */
object BookMapper {

    // region BookDto → domínio / cache

    fun BookDto.toDomain(): Book = Book(
        idLivro    = idLivro,
        nome       = nome,
        autor      = autor,
        numPag     = numPag,
        numPagRead = numPagRead,
        status     = BookStatus.fromDbValue(status),
        ano        = ano,
        text       = text,
        dtInicial  = dtInicial,
        dtFinal    = dtFinal,
        img        = img
    )

    fun BookDto.toEntity(cachedAt: Long = System.currentTimeMillis()): BookEntity = BookEntity(
        idLivro    = idLivro,
        nome       = nome,
        autor      = autor,
        numPag     = numPag,
        numPagRead = numPagRead,
        status     = status,
        ano        = ano,
        text       = text,
        dtInicial  = dtInicial,
        dtFinal    = dtFinal,
        img        = img,
        cachedAt   = cachedAt
    )

    // endregion

    // region BookEntity → domínio

    fun BookEntity.toDomain(): Book = Book(
        idLivro    = idLivro,
        nome       = nome,
        autor      = autor,
        numPag     = numPag,
        numPagRead = numPagRead,
        status     = BookStatus.fromDbValue(status),
        ano        = ano,
        text       = text,
        dtInicial  = dtInicial,
        dtFinal    = dtFinal,
        img        = img
    )

    // endregion

    // region Book → cache (usado ao gravar um livro recém-criado no cache)

    fun Book.toEntity(cachedAt: Long = System.currentTimeMillis()): BookEntity = BookEntity(
        idLivro    = idLivro,
        nome       = nome,
        autor      = autor,
        numPag     = numPag,
        numPagRead = numPagRead,
        status     = status.label,
        ano        = ano,
        text       = text,
        dtInicial  = dtInicial,
        dtFinal    = dtFinal,
        img        = img,
        cachedAt   = cachedAt
    )

    // endregion
}