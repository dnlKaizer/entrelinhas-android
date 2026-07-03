package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.entrelinhas.data.mock.readBooks
import com.br.entrelinhas.data.mock.readingBooks
import com.br.entrelinhas.data.mock.wishedBooks
import com.br.entrelinhas.ui.state.BooksUiState
import com.br.entrelinhas.ui.theme.EntrelinhasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: BooksUiState,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBookClick: (Int) -> Unit,
    onCreateBook: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Entrelinhas",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    // Mostra indicador de refresh enquanto sincroniza em background
                    if (uiState is BooksUiState.Success && uiState.isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 4.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar tema"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateBook,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Cadastrar Livro") }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is BooksUiState.Loading -> LoadingState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            is BooksUiState.Error -> ErrorState(
                message  = uiState.message,
                onRetry  = onRetry,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )

            is BooksUiState.Success -> BookListContent(
                lendoBooks    = uiState.lendoBooks,
                desejadoBooks = uiState.desejadoBooks,
                lidoBooks     = uiState.lidoBooks,
                onBookClick   = onBookClick,
                modifier      = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true, name = "Home – Success Light")
@Composable
private fun HomeSuccessPreview() {
    EntrelinhasTheme(darkTheme = false) {
        HomeContent(
            uiState = BooksUiState.Success(
                lendoBooks    = readingBooks,
                desejadoBooks = wishedBooks,
                lidoBooks     = readBooks
            ),
            isDarkTheme   = false,
            onToggleTheme = {},
            onBookClick   = {},
            onCreateBook  = {},
            onRetry       = {},
            onRefresh     = {}
        )
    }
}

@Preview(showBackground = true, name = "Home – Loading")
@Composable
private fun HomeLoadingPreview() {
    EntrelinhasTheme(darkTheme = false) {
        HomeContent(
            uiState       = BooksUiState.Loading,
            isDarkTheme   = false,
            onToggleTheme = {},
            onBookClick   = {},
            onCreateBook  = {},
            onRetry       = {},
            onRefresh     = {}
        )
    }
}

@Preview(showBackground = true, name = "Home – Error")
@Composable
private fun HomeErrorPreview() {
    EntrelinhasTheme(darkTheme = false) {
        HomeContent(
            uiState       = BooksUiState.Error("Sem conexão com a internet."),
            isDarkTheme   = false,
            onToggleTheme = {},
            onBookClick   = {},
            onCreateBook  = {},
            onRetry       = {},
            onRefresh     = {}
        )
    }
}