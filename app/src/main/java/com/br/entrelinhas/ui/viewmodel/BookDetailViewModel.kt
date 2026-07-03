package com.br.entrelinhas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.br.entrelinhas.data.repository.BookRepository
import com.br.entrelinhas.ui.state.BookDetailUiState
import com.br.entrelinhas.ui.state.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de Detalhes do Livro.
 * Busca o livro pelo [bookId] — cache local primeiro, remoto como fallback.
 */
class BookDetailViewModel(
    private val bookId: Int,
    private val repository: BookRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
    val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

    init { loadBook() }

    fun loadBook() {
        viewModelScope.launch {
            _uiState.value = BookDetailUiState.Loading
            try {
                val book = repository.getBookById(bookId)
                _uiState.value = if (book != null) {
                    BookDetailUiState.Success(book)
                } else {
                    BookDetailUiState.NotFound
                }
            } catch (e: Exception) {
                _uiState.value = BookDetailUiState.Error(e.toUserMessage())
            }
        }
    }
}

class BookDetailViewModelFactory(
    private val bookId: Int,
    private val repository: BookRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        BookDetailViewModel(bookId, repository) as T
}
