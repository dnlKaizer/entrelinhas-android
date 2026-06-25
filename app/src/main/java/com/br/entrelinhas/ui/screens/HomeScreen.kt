package com.br.entrelinhas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.br.entrelinhas.data.model.BookStatus
import com.br.entrelinhas.ui.components.BookSection
import com.br.entrelinhas.ui.theme.EntrelinhasTheme

/**
 * Tela Home: dashboard principal do app, com três seções de livros (Lendo, Desejado, Lido).
 * Ao tocar em um livro, [onBookClick] é chamado com o id do livro para navegar até os detalhes.
 *
 * [isDarkTheme] e [onToggleTheme] vêm "hoisted" (elevados) de MainActivity, demonstrando o
 * padrão de State Hoisting com remember + mutableStateOf.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBookClick: (Int) -> Unit,
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
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar tema"
                        )
                    }
                    // Botão sem funcionalidade nesta etapa (apenas frontend, sem autenticação).
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    text = "Minha Biblioteca",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                BookSection(
                    title = "Lendo",
                    status = BookStatus.LENDO,
                    books = readingBooks,
                    onBookClick = onBookClick
                )
            }
            item {
                BookSection(
                    title = "Desejado",
                    status = BookStatus.DESEJADO,
                    books = wishedBooks,
                    onBookClick = onBookClick
                )
            }
            item {
                BookSection(
                    title = "Lido",
                    status = BookStatus.LIDO,
                    books = readBooks,
                    onBookClick = onBookClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    EntrelinhasTheme(darkTheme = false) {
        HomeScreen(isDarkTheme = false, onToggleTheme = {}, onBookClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    EntrelinhasTheme(darkTheme = true) {
        HomeScreen(isDarkTheme = true, onToggleTheme = {}, onBookClick = {})
    }
}