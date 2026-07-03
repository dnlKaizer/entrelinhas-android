package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.model.BookStatus
import com.br.entrelinhas.ui.theme.statusContainerColors

/**
 * Seção da Home (Lendo / Desejado / Lido): um Card com título, contagem de livros
 * e uma LazyRow horizontal com o BookCard de cada livro.
 */
@Composable
fun BookSection(
    title: String,
    status: BookStatus,
    books: List<Book>,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val (containerColor, onContainerColor) = statusContainerColors(status)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = onContainerColor
            )
            Text(
                text = "${books.size} ${if (books.size == 1) "livro" else "livros"}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Normal,
                color = onContainerColor
            )
        }

        if (books.isEmpty()) {
            Text(
                text = "Nenhum livro nesta lista ainda.",
                style = MaterialTheme.typography.bodyMedium,
                color = onContainerColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = books, key = { it.idLivro }) { book ->
                    BookCard(book = book, onClick = { onBookClick(book.idLivro) })
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}