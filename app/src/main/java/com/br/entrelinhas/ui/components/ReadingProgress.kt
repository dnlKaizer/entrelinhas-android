package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.entrelinhas.ui.theme.EntrelinhasTheme

/**
 * Componente reutilizável que mostra o progresso de leitura de um livro:
 * barra de progresso (LinearProgressIndicator), percentual e o texto
 * "X de Y páginas lidas".
 */
@Composable
fun ReadingProgress(
    pagesRead: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    val percent = remember(pagesRead, totalPages) {
        if (totalPages > 0) {
            ((pagesRead.toFloat() / totalPages.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { percent / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$pagesRead de $totalPages páginas lidas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingProgressPreview() {
    EntrelinhasTheme(darkTheme = false) {
        ReadingProgress(pagesRead = 120, totalPages = 350)
    }
}