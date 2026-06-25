package com.br.entrelinhas.data.model

/** Espelha o enum "status_livro" do Supabase: "Desejado" | "Lendo" | "Lido". */
enum class BookStatus(val label: String) {
    LENDO("Lendo"),
    DESEJADO("Desejado"),
    LIDO("Lido");

    companion object {
        fun fromDbValue(value: String): BookStatus =
            entries.firstOrNull { it.label == value } ?: DESEJADO
    }
}

/**
 * Modelo de domínio correspondente à tabela "Livro" do Supabase.
 * É o único tipo de Book que a UI conhece — nunca expõe BookEntity ou BookDto.
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
    val dtInicial: String?,   // ISO "yyyy-MM-dd" ou null
    val dtFinal: String?,     // ISO "yyyy-MM-dd" ou null
    val img: String?          // URL pública da capa ou null
) {
    val progressPercent: Int
        get() = if (numPag > 0) {
            ((numPagRead.toFloat() / numPag) * 100).toInt().coerceIn(0, 100)
        } else 0
}

/** Formata data ISO "yyyy-MM-dd" → "dd/MM/yyyy", ou "Não informado" se nula. */
fun formatBookDate(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return "Não informado"
    val parts = isoDate.split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else isoDate
}