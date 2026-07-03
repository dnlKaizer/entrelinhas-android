package com.br.entrelinhas.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onOpenPicker: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // permite digitação manual no formato dd/MM/yyyy
            val filtered = input.filter { it.isDigit() || it == '/' }
            onValueChange(filtered)
        },
        label = { Text(label) },
        placeholder = { Text("dd/mm/aaaa") },
        singleLine = true,
        enabled = enabled,
        trailingIcon = {
            IconButton(onClick = onOpenPicker, enabled = enabled) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Abrir calendário")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.fillMaxWidth()
    )
}