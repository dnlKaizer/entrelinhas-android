package com.br.entrelinhas.data.model

/**
 * Equivalente ao enum "status_livro" gerado pelo Supabase em database.types.ts:
 *   status_livro: "Desejado" | "Lendo" | "Lido"
 */
enum class BookStatus(val label: String) {
    LENDO("Lendo"),
    DESEJADO("Desejado"),
    LIDO("Lido")
}

/**
 * Modelo Kotlin equivalente à tabela "Livro" do Supabase (database.types.ts).
 * Nesta etapa não há backend/Supabase: os valores vêm apenas de data/mock/MockBooks.kt.
 *
 * Campos originais (TypeScript) -> Kotlin:
 *   idLivro, nome, autor, numPag, numPagRead, status, ano, text, dtInicial, dtFinal, img
 */
data class Book(
    val idLivro: Int,
    val nome: String,
    val autor: String?,
    val numPag: Int,
    val numPagRead: Int,
    val status: BookStatus,
    val ano: Int?,
    val text: String?,
    val dtInicial: String?, // formato ISO "yyyy-MM-dd", ou null se não informado
    val dtFinal: String?,   // formato ISO "yyyy-MM-dd", ou null se não informado
    val img: String?,       // URL pública (fictícia) da capa
    val idUsuario: String
) {
    /** Percentual de páginas lidas, de 0 a 100. */
    val progressPercent: Int
        get() = if (numPag > 0) {
            ((numPagRead.toFloat() / numPag.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
}

/**
 * Converte uma data no formato ISO "yyyy-MM-dd" para "dd/MM/yyyy".
 * Retorna "Não informado" caso a data seja nula ou vazia.
 */
fun formatBookDate(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return "Não informado"
    val parts = isoDate.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else isoDate
}