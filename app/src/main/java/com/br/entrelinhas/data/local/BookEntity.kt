package com.br.entrelinhas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa um livro no banco local (SQLite).
 * O campo [cachedAt] registra o timestamp do último cache, usado para validar
 * se os dados ainda estão frescos antes de ir à rede.
 *
 * Status é armazenado como String para evitar conversores de tipo — o mapeamento
 * para BookStatus é feito no BookMapper.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val idLivro: Int,
    val nome: String,
    val autor: String?,
    val numPag: Int,
    val numPagRead: Int,
    val status: String,       // "Lendo" | "Desejado" | "Lido"
    val ano: Int?,
    val text: String?,
    val dtInicial: String?,
    val dtFinal: String?,
    val img: String?,
    val cachedAt: Long = System.currentTimeMillis()
)