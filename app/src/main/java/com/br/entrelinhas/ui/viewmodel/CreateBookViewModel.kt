package com.br.entrelinhas.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.br.entrelinhas.data.repository.BookRepository
import com.br.entrelinhas.data.remote.StorageService
import com.br.entrelinhas.ui.state.CreateBookUiState
import com.br.entrelinhas.ui.state.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de Cadastro de Livro.
 *
 * Usa [AndroidViewModel] para acessar o [Application.applicationContext] necessário
 * ao [StorageService] para leitura do ContentResolver (URI da imagem da galeria).
 *
 * Fluxo de criação:
 *  1. Usuário seleciona imagem → [onImageSelected]
 *  2. Usuário confirma formulário → [createBook]
 *     2a. Upload da imagem para o Supabase Storage → URL pública
 *     2b. Inserção do livro no Supabase com a URL
 *  3. Emite [CreateBookUiState.isSuccess] = true para que a tela navegue de volta
 */
class CreateBookViewModel(
    application: Application,
    private val repository: BookRepository,
    private val storageService: StorageService
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CreateBookUiState())
    val uiState: StateFlow<CreateBookUiState> = _uiState.asStateFlow()

    /** Armazena a URI selecionada pelo usuário na galeria para preview e posterior upload. */
    fun onImageSelected(uri: Uri) {
        _uiState.update { it.copy(selectedImageUri = uri, uploadedImageUrl = null) }
    }

    /** Remove a imagem selecionada (botão "trocar/remover capa"). */
    fun onImageRemoved() {
        _uiState.update { it.copy(selectedImageUri = null, uploadedImageUrl = null) }
    }

    /** Descarta uma mensagem de erro já exibida. */
    fun onErrorDismissed() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Executa o fluxo completo de criação:
     *  1. Valida os campos obrigatórios
     *  2. Faz upload da imagem (se houver)
     *  3. Insere o livro no Supabase
     *
     * Os parâmetros refletem os campos do formulário já convertidos para os tipos corretos.
     */
    fun createBook(
        nome: String,
        autor: String,
        numPagStr: String,
        status: String,
        anoStr: String,
        descricao: String
    ) {
        // Validação dos campos obrigatórios
        val validationError = validate(nome, numPagStr)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        val numPag = numPagStr.trim().toIntOrNull() ?: 0
        val ano = anoStr.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            try {
                // Passo 1: upload da imagem (se houver URI selecionada)
                val imageUrl: String? = _uiState.value.selectedImageUri?.let { uri ->
                    _uiState.update { it.copy(isUploadingImage = true) }
                    val url = storageService.uploadBookCover(uri, getApplication())
                    _uiState.update { it.copy(isUploadingImage = false, uploadedImageUrl = url) }
                    url
                }

                // Passo 2: inserção do livro com a URL da capa (ou null se sem imagem)
                repository.createBook(
                    nome       = nome.trim(),
                    autor      = autor.trim().takeIf { it.isNotEmpty() },
                    numPag     = numPag,
                    status     = status,
                    ano        = ano,
                    text       = descricao.trim().takeIf { it.isNotEmpty() },
                    imageUrl   = imageUrl
                )

                _uiState.update { it.copy(isSaving = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving        = false,
                        isUploadingImage = false,
                        errorMessage    = e.toUserMessage()
                    )
                }
            }
        }
    }

    private fun validate(nome: String, numPagStr: String): String? {
        if (nome.isBlank()) return "O título do livro é obrigatório."
        val numPag = numPagStr.trim().toIntOrNull()
        if (numPagStr.isBlank()) return "O número de páginas é obrigatório."
        if (numPag == null || numPag <= 0) return "Informe um número de páginas válido (inteiro positivo)."
        return null
    }
}

/** Factory manual (sem Hilt) que injeta Repository e StorageService no CreateBookViewModel. */
class CreateBookViewModelFactory(
    private val application: Application,
    private val repository: BookRepository,
    private val storageService: StorageService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        CreateBookViewModel(application, repository, storageService) as T
}