package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.br.entrelinhas.data.config.SupabaseConfig
import com.br.entrelinhas.data.mock.readingBooks
import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.ui.theme.EntrelinhasTheme

private val CARD_WIDTH = 140.dp
private val COVER_HEIGHT = 180.dp

/**
 * Componente reutilizável que representa um livro dentro de uma lista (LazyRow).
 * Exibe capa, título e autor, e dispara [onClick] para abrir a tela de detalhes.
 */
@Composable
fun BookCard(
    book: Book,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coverImg = SupabaseConfig.getCoverUrl(book.img);

    Card(
        onClick = onClick,
        modifier = modifier.width(CARD_WIDTH),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = coverImg,
                contentDescription = "Capa do livro ${book.nome}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(COVER_HEIGHT)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.nome,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = book.autor ?: "Autor desconhecido",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookCardPreview() {
    EntrelinhasTheme(darkTheme = true) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BookCard(
                book = readingBooks.first(),
                onClick = {},
                modifier = Modifier.padding(PaddingValues(0.dp))
            )
        }
    }
}
