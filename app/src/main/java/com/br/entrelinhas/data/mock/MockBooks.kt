package com.br.entrelinhas.data.mock

import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.model.BookStatus

/**
 * Dados mockados para a etapa de frontend (sem backend/Supabase/persistência).
 */

val readingBooks: List<Book> = listOf(
    Book(
        idLivro = 51,
        nome = "Introduction to Algorithms",
        numPag = 1312,
        status = BookStatus.LENDO,
        autor = "Thomas H. Cormen",
        ano = 2009,
        text = "Referência completa sobre algoritmos, estruturas de dados e análise de complexidade.",
        dtInicial = "2026-02-02",
        dtFinal = null,
        numPagRead = 456,
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4ulpn-3ve8xk.jpg"
    ),
    Book(
        idLivro = 52,
        nome = "Design Patterns: Elements of Reusable Object-Oriented Software",
        numPag = 395,
        status = BookStatus.LENDO,
        autor = "Erich Gamma",
        ano = 1994,
        text = "Clássico que define padrões de projeto reutilizáveis em programação orientada a objetos.",
        dtInicial = "2026-03-28",
        dtFinal = null,
        numPagRead = 144,
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4wypm-ouipcy.jpg"
    )
)

val wishedBooks: List<Book> = listOf(
    Book(
        idLivro = 50,
        nome = "Structure and Interpretation of Computer Programs",
        numPag = 657,
        status = BookStatus.DESEJADO,
        autor = "Harold Abelson",
        ano = 1996,
        text = "Livro profundo sobre fundamentos da computação usando programação funcional.",
        dtInicial = null,
        dtFinal = null,
        numPagRead = 0,
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4stst-vbaonx.jpg"
    ),
    Book(
        idLivro = 49,
        nome = "O Programador Pragmático",
        numPag = 352,
        status = BookStatus.DESEJADO,
        autor = "Andrew Hunt",
        ano = 1999,
        text = "Livro sobre mentalidade e boas práticas para desenvolvedores, indo além do código.",
        dtInicial = null,
        dtFinal = null,
        numPagRead = 0,
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4r2w1-f3jccd.jpg"
    )
)

val readBooks: List<Book> = listOf(
    Book(
        idLivro = 47,
        nome = "Clean Code",
        numPag = 464,
        status = BookStatus.LIDO,
        autor = "Robert C. Martin",
        ano = 2008,
        text = "Guia essencial sobre boas práticas de programação, focando em código limpo, legível e sustentável.",
        dtInicial = "2025-01-01",
        dtFinal = "2025-03-01",
        numPagRead = 464,
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4n5e3-kcz0zn.jpg"
    ),
    Book(
        idLivro = 48,
        nome = "Code Complete",
        numPag = 960,
        status = BookStatus.LIDO,
        autor = "Steve McConnell",
        numPagRead = 960,
        ano = 2004,
        text = "Guia abrangente sobre construção de software com foco em qualidade e produtividade.",
        dtInicial = "2025-03-02",
        dtFinal = "2025-05-31",
        img = "51511986-9897-4fe6-bcd0-f0eb6ad4f061/mnw4prbl-z39xik.jpg"
    )
)

/** Lista com todos os livros mockados, usada para localizar um livro pelo id na tela de detalhes. */
val allMockBooks: List<Book> = readingBooks + wishedBooks + readBooks

fun findMockBookById(id: Int): Book? = allMockBooks.find { it.idLivro == id }
