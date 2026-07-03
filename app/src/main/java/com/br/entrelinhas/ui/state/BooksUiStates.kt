package com.br.entrelinhas.ui.state

import com.br.entrelinhas.data.model.Book

// ──────────────────────────────────────────────────────────────────
// Estados da tela Home
// ──────────────────────────────────────────────────────────────────

/**
 * Estado da lista de livros exposta pelo HomeViewModel via StateFlow.
 *
 *  Loading  → carregando dados (sem cache ou primeira abertura)
 *  Success  → dados disponíveis, separados por status
 *  Error    → falha ao carregar e sem cache de fallback
 */
sealed interface BooksUiState {
    data object Loading : BooksUiState

    data class Success(
        val lendoBooks: List<Book>,
        val desejadoBooks: List<Book>,
        val lidoBooks: List<Book>,
        /** true enquanto o background refresh ainda está em andamento */
        val isRefreshing: Boolean = false
    ) : BooksUiState

    data class Error(val message: String) : BooksUiState
}

// ──────────────────────────────────────────────────────────────────
// Estado da tela de Detalhes
// ──────────────────────────────────────────────────────────────────

sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState
    data class Success(val book: Book) : BookDetailUiState
    data class Error(val message: String) : BookDetailUiState
    data object NotFound : BookDetailUiState
}

// ──────────────────────────────────────────────────────────────────
// Estado da tela de Cadastro
// ──────────────────────────────────────────────────────────────────

data class CreateBookUiState(
    val isUploadingImage: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    /** URI local da imagem selecionada na galeria (antes do upload). */
    val selectedImageUri: android.net.Uri? = null,
    /** URL pública da capa após upload bem-sucedido para o Supabase Storage. */
    val uploadedImageUrl: String? = null
)

// ──────────────────────────────────────────────────────────────────
// Extensão de utilidade para formatar mensagens de erro
// ──────────────────────────────────────────────────────────────────

fun Throwable.toUserMessage(): String = when {
    message?.contains("network", ignoreCase = true) == true ||
            message?.contains("UnknownHost", ignoreCase = true) == true ||
            message?.contains("Unable to resolve host", ignoreCase = true) == true ->
        "Sem conexão com a internet. Verifique sua rede."

    message?.contains("timeout", ignoreCase = true) == true ->
        "O servidor demorou para responder. Tente novamente."

    message?.contains("401", ignoreCase = true) == true ||
            message?.contains("403", ignoreCase = true) == true ->
        "Sem permissão para realizar esta operação."

    message?.contains("404", ignoreCase = true) == true ->
        "Recurso não encontrado."

    message?.contains("storage", ignoreCase = true) == true ||
            message?.contains("upload", ignoreCase = true) == true ->
        "Falha no upload da imagem. Tente novamente."

    else -> "Erro inesperado: ${message ?: "desconhecido"}"
}