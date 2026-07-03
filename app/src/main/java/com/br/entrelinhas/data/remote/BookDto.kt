package com.br.entrelinhas.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO para leitura da tabela "Livro" do Supabase.
 * Campos exatamente como definidos no banco (camelCase conforme database.types.ts).
 * idLivro tem default 0 porque em selects o Supabase sempre retorna o campo,
 * mas precisamos de um valor seguro ao deserializar.
 */
@Serializable
data class BookDto(
    @SerialName("idLivro")    val idLivro: Int = 0,
    @SerialName("idUsuario")  val idUsuario: String = "",
    @SerialName("nome")       val nome: String = "",
    @SerialName("numPag")     val numPag: Int = 0,
    @SerialName("numPagRead") val numPagRead: Int = 0,
    @SerialName("status")     val status: String = "Desejado",
    @SerialName("autor")      val autor: String? = null,
    @SerialName("ano")        val ano: Int? = null,
    @SerialName("text")       val text: String? = null,
    @SerialName("dtInicial")  val dtInicial: String? = null,
    @SerialName("dtFinal")    val dtFinal: String? = null,
    @SerialName("img")        val img: String? = null
)

/**
 * DTO para inserção na tabela "Livro".
 * idLivro é omitido (auto-gerado pelo banco).
 */
@Serializable
data class BookInsertDto(
    @SerialName("idUsuario")  val idUsuario: String,
    @SerialName("nome")       val nome: String,
    @SerialName("numPag")     val numPag: Int,
    @SerialName("numPagRead") val numPagRead: Int = 0,
    @SerialName("status")     val status: String,
    @SerialName("autor")      val autor: String? = null,
    @SerialName("ano")        val ano: Int? = null,
    @SerialName("text")       val text: String? = null,
    @SerialName("dtInicial")  val dtInicial: String? = null,
    @SerialName("dtFinal")    val dtFinal: String? = null,
    @SerialName("img")        val img: String? = null
)