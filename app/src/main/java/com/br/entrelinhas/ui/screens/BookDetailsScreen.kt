package com.br.entrelinhas.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.br.entrelinhas.data.config.SupabaseConfig
import com.br.entrelinhas.data.mock.findMockBookById
import com.br.entrelinhas.data.mock.readingBooks
import com.br.entrelinhas.data.model.formatBookDate
import com.br.entrelinhas.ui.components.ReadingProgress
import com.br.entrelinhas.ui.theme.EntrelinhasTheme
import com.br.entrelinhas.ui.theme.statusContainerColors

/**
 * Tela de detalhes de um livro. Busca o livro pelo [bookId] na lista de dados mockados
 * (sem backend/Supabase nesta etapa) e exibe capa, status, progresso de leitura,
 * estatísticas, datas e a descrição completa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    bookId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val book = remember(bookId) { findMockBookById(bookId) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Livro") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (book == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Livro não encontrado.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            val coverUrl = SupabaseConfig.getCoverUrl(book.img)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Capa do livro
                Card(
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    AsyncImage(
                        model = coverUrl,
                        contentDescription = "Capa do livro ${book.nome}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = book.nome,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.autor ?: "Autor desconhecido",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status (Lendo / Desejado / Lido)
                val (statusContainer, onStatusContainer) = statusContainerColors(book.status)
                Surface(
                    color = statusContainer,
                    contentColor = onStatusContainer,
                    shape = RoundedCornerShape(percent = 50)
                ) {
                    Text(
                        text = book.status.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Progresso de leitura
                ReadingProgress(
                    pagesRead = book.numPagRead,
                    totalPages = book.numPag,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Estatísticas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BookStatCard(
                        icon = Icons.Default.MenuBook,
                        label = "Total de páginas",
                        value = "${book.numPag}",
                        modifier = Modifier.weight(1f)
                    )
                    BookStatCard(
                        icon = Icons.Default.Bookmark,
                        label = "Páginas lidas",
                        value = "${book.numPagRead}",
                        modifier = Modifier.weight(1f)
                    )
                    BookStatCard(
                        icon = Icons.Default.Layers,
                        label = "Ano de publicação",
                        value = book.ano?.toString() ?: "Não informado",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Datas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BookStatCard(
                        icon = Icons.Default.CalendarMonth,
                        label = "Início da leitura",
                        value = formatBookDate(book.dtInicial),
                        modifier = Modifier.weight(1f)
                    )
                    BookStatCard(
                        icon = Icons.Default.EmojiEvents,
                        label = "Término da leitura",
                        value = formatBookDate(book.dtFinal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(20.dp))

                // Descrição
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Descrição",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.text ?: "Nenhuma descrição cadastrada para este livro.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/** Pequeno card de estatística usado na tela de detalhes (ícone + valor + rótulo). */
@Composable
private fun BookStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenPreview() {
    EntrelinhasTheme(darkTheme = false) {
        BookDetailsScreen(bookId = readingBooks.first().idLivro, onBackClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailsScreenDarkPreview() {
    EntrelinhasTheme(darkTheme = true) {
        BookDetailsScreen(bookId = readingBooks.first().idLivro, onBackClick = {})
    }
}
