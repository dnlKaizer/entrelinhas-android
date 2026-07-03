package com.br.entrelinhas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.br.entrelinhas.data.model.BookStatus
import com.br.entrelinhas.data.repository.BookRepository
import com.br.entrelinhas.ui.state.BooksUiState
import com.br.entrelinhas.ui.state.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel da tela Home.
 *
 * Responsabilidades:
 *  - Buscar livros via [BookRepository] (respeitando estratégia de cache)
 *  - Disparar refresh em background quando o cache foi retornado
 *  - Expor [uiState] como StateFlow imutável para a UI
 *  - Tratar erros e traduzir para mensagens amigáveis
 */
class HomeViewModel(private val repository: BookRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        loadBooks()
    }

    /**
     * Carrega livros aplicando a estratégia cache-first do repositório.
     * Se o cache for retornado, dispara refresh em background automaticamente.
     */
    fun loadBooks() {
        viewModelScope.launch {
            _uiState.value = BooksUiState.Loading
            try {
                val (books, fromCache) = repository.getBooks()
                _uiState.value = BooksUiState.Success(
                    lendoBooks    = books.filter { it.status == BookStatus.LENDO },
                    desejadoBooks = books.filter { it.status == BookStatus.DESEJADO },
                    lidoBooks     = books.filter { it.status == BookStatus.LIDO },
                    isRefreshing  = fromCache // se veio do cache, indica que está atualizando
                )

                if (fromCache) {
                    // Background refresh: atualiza em segundo plano sem travar a UI
                    refreshInBackground()
                }
            } catch (e: Exception) {
                _uiState.value = BooksUiState.Error(e.toUserMessage())
            }
        }
    }

    /** Chamado externamente (pull-to-refresh ou retry) para forçar atualização. */
    fun refresh() {
        val current = _uiState.value
        if (current is BooksUiState.Success) {
            _uiState.update { (it as BooksUiState.Success).copy(isRefreshing = true) }
        } else {
            _uiState.value = BooksUiState.Loading
        }
        viewModelScope.launch {
            try {
                val fresh = repository.refreshBooks()
                _uiState.value = BooksUiState.Success(
                    lendoBooks    = fresh.filter { it.status == BookStatus.LENDO },
                    desejadoBooks = fresh.filter { it.status == BookStatus.DESEJADO },
                    lidoBooks     = fresh.filter { it.status == BookStatus.LIDO },
                    isRefreshing  = false
                )
            } catch (e: Exception) {
                // Se já havia dados em tela, mantém e só mostra o erro de refresh
                if (_uiState.value is BooksUiState.Loading) {
                    _uiState.value = BooksUiState.Error(e.toUserMessage())
                } else {
                    _uiState.update { state ->
                        if (state is BooksUiState.Success) state.copy(isRefreshing = false) else state
                    }
                }
            }
        }
    }

    /** Chamado pelo CreateBookViewModel após criação de um livro para atualizar a lista. */
    fun onBookCreated() = refresh()

    private fun refreshInBackground() {
        viewModelScope.launch {
            try {
                val fresh = repository.refreshBooks()
                _uiState.value = BooksUiState.Success(
                    lendoBooks    = fresh.filter { it.status == BookStatus.LENDO },
                    desejadoBooks = fresh.filter { it.status == BookStatus.DESEJADO },
                    lidoBooks     = fresh.filter { it.status == BookStatus.LIDO },
                    isRefreshing  = false
                )
            } catch (_: Exception) {
                // Falha silenciosa no refresh em background — cache já foi exibido
                _uiState.update { state ->
                    if (state is BooksUiState.Success) state.copy(isRefreshing = false) else state
                }
            }
        }
    }
}

/** Factory manual (sem Hilt) para injetar BookRepository no HomeViewModel. */
class HomeViewModelFactory(private val repository: BookRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        HomeViewModel(repository) as T
}