package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.br.entrelinhas.data.model.Book
import com.br.entrelinhas.data.model.BookStatus

@Composable
fun BookListContent(
    lendoBooks: List<Book>,
    desejadoBooks: List<Book>,
    lidoBooks: List<Book>,
    onBookClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
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
                title       = "Lendo",
                status      = BookStatus.LENDO,
                books       = lendoBooks,
                onBookClick = onBookClick
            )
        }
        item {
            BookSection(
                title       = "Desejado",
                status      = BookStatus.DESEJADO,
                books       = desejadoBooks,
                onBookClick = onBookClick
            )
        }
        item {
            BookSection(
                title       = "Lido",
                status      = BookStatus.LIDO,
                books       = lidoBooks,
                onBookClick = onBookClick
            )
        }
    }
}