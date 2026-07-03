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
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.br.entrelinhas.EntrelinhasApplication
import com.br.entrelinhas.data.config.SupabaseConfig
import com.br.entrelinhas.data.mock.readingBooks
import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.model.formatBookDate
import com.br.entrelinhas.data.model.getCoverUrl
import com.br.entrelinhas.ui.components.ReadingProgress
import com.br.entrelinhas.ui.state.BookDetailUiState
import com.br.entrelinhas.ui.theme.EntrelinhasTheme
import com.br.entrelinhas.ui.theme.statusContainerColors
import com.br.entrelinhas.ui.viewmodel.BookDetailViewModel
import com.br.entrelinhas.ui.viewmodel.BookDetailViewModelFactory

@Composable
fun BookDetailsScreen(
    bookId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as EntrelinhasApplication
    val viewModel: BookDetailViewModel = viewModel(
        key     = "book_$bookId",
        factory = BookDetailViewModelFactory(bookId, app.bookRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    BookDetailsContent(
        uiState     = uiState,
        onBackClick = onBackClick,
        onRetry     = { viewModel.loadBook() },
        modifier    = modifier
    )
}

// ─── Conteúdo (previewável) ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsContent(
    uiState: BookDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        when (uiState) {
            is BookDetailUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is BookDetailUiState.NotFound -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Livro não encontrado.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            is BookDetailUiState.Error -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = onRetry) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Tentar novamente")
                    }
                }
            }

            is BookDetailUiState.Success -> BookDetailsBody(
                book = uiState.book,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun BookDetailsBody(book: Book, modifier: Modifier = Modifier) {
    val coverUrl = getCoverUrl(book.img)

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Capa
        Card(
            modifier = Modifier.width(200.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            AsyncImage(
                model              = coverUrl,
                contentDescription = "Capa de ${book.nome}",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // Título e autor
        Text(
            text      = book.nome,
            style     = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text      = book.autor ?: "Autor desconhecido",
            style     = MaterialTheme.typography.bodyLarge,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        // Badge de status
        val (statusBg, statusFg) = statusContainerColors(book.status)
        Surface(
            color        = statusBg,
            contentColor = statusFg,
            shape        = RoundedCornerShape(percent = 50)
        ) {
            Text(
                text      = book.status.label,
                style     = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier  = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        // Progresso
        ReadingProgress(
            pagesRead  = book.numPagRead,
            totalPages = book.numPag,
            modifier   = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // Estatísticas: páginas, páginas lidas, ano
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookStatCard(
                icon     = Icons.Default.MenuBook,
                label    = "Total de páginas",
                value    = "${book.numPag}",
                modifier = Modifier.weight(1f)
            )
            BookStatCard(
                icon     = Icons.Default.Bookmark,
                label    = "Páginas lidas",
                value    = "${book.numPagRead}",
                modifier = Modifier.weight(1f)
            )
            BookStatCard(
                icon     = Icons.Default.Layers,
                label    = "Ano",
                value    = book.ano?.toString() ?: "—",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Datas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookStatCard(
                icon     = Icons.Default.CalendarMonth,
                label    = "Início da leitura",
                value    = formatBookDate(book.dtInicial),
                modifier = Modifier.weight(1f)
            )
            BookStatCard(
                icon     = Icons.Default.EmojiEvents,
                label    = "Término da leitura",
                value    = formatBookDate(book.dtFinal),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(20.dp))

        // Descrição
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text       = "Descrição",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = book.text ?: "Nenhuma descrição cadastrada.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun BookStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = value,
                style     = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Text(
                text      = label,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines  = 2
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Details – Success")
@Composable
private fun BookDetailsSuccessPreview() {
    EntrelinhasTheme(darkTheme = false) {
        BookDetailsContent(
            uiState     = BookDetailUiState.Success(readingBooks.first()),
            onBackClick = {},
            onRetry     = {}
        )
    }
}

@Preview(showBackground = true, name = "Details – Loading")
@Composable
private fun BookDetailsLoadingPreview() {
    EntrelinhasTheme(darkTheme = false) {
        BookDetailsContent(
            uiState     = BookDetailUiState.Loading,
            onBackClick = {},
            onRetry     = {}
        )
    }
}