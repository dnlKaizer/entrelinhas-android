package com.br.entrelinhas.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    onDateSelected(sdf.format(Date(millis)))
                }
                onDismiss()
            }) { Text("Confirmar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        // O próprio DatePicker já traz o ícone de alternância
        // entre calendário e campo de texto (canto superior direito)
        DatePicker(state = datePickerState)
    }
}

fun formatToSupabase(dateStr: String): String {
    return try {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = inputFormat.parse(dateStr)
        if (date != null) outputFormat.format(date) else ""
    } catch (e: Exception) {
        "" // Se a data estiver incompleta ou inválida na digitação, envia vazio
    }
}