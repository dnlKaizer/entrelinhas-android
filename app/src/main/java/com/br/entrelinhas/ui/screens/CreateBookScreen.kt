package com.br.entrelinhas.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.br.entrelinhas.EntrelinhasApplication
import com.br.entrelinhas.data.model.BookStatus
import com.br.entrelinhas.ui.state.CreateBookUiState
import com.br.entrelinhas.ui.theme.EntrelinhasTheme
import com.br.entrelinhas.ui.viewmodel.CreateBookViewModel

// ─── Tela (com ViewModel) ─────────────────────────────────────────────────────

@Composable
fun CreateBookScreen(
    onBackClick: () -> Unit,
    onBookCreated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as EntrelinhasApplication
    val viewModel: CreateBookViewModel = viewModel(
        factory = app.createBookViewModelFactory
    )
    val uiState by viewModel.uiState.collectAsState()

    // Navega de volta assim que o livro for criado com sucesso
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBookCreated()
        }
    }

    CreateBookContent(
        uiState          = uiState,
        onBackClick      = onBackClick,
        onImageSelected  = { viewModel.onImageSelected(it) },
        onImageRemoved   = { viewModel.onImageRemoved() },
        onErrorDismissed = { viewModel.onErrorDismissed() },
        onSubmit         = { nome, autor, numPag, status, ano, desc ->
            viewModel.createBook(nome, autor, numPag, status, ano, desc)
        },
        modifier = modifier
    )
}

// ─── Conteúdo (previewável) ───────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookContent(
    uiState: CreateBookUiState,
    onBackClick: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: () -> Unit,
    onErrorDismissed: () -> Unit,
    onSubmit: (nome: String, autor: String, numPag: String, status: String, ano: String, desc: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado local do formulário (state hoisting para os campos de texto)
    var nome      by remember { mutableStateOf("") }
    var autor     by remember { mutableStateOf("") }
    var numPag    by remember { mutableStateOf("") }
    var ano       by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var status    by remember { mutableStateOf(BookStatus.DESEJADO) }

    // Erros de validação locais (exibidos inline, sem aguardar submit)
    var nomeError   by remember { mutableStateOf<String?>(null) }
    var numPagError by remember { mutableStateOf<String?>(null) }

    val isLoading = uiState.isUploadingImage || uiState.isSaving

    // Launcher para a galeria nativa (API moderna: PickVisualMedia)
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { onImageSelected(it) } }

    // Diálogo de erro vindo do ViewModel (rede, upload, etc.)
    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = onErrorDismissed,
            title   = { Text("Erro") },
            text    = { Text(uiState.errorMessage) },
            confirmButton = {
                TextButton(onClick = onErrorDismissed) { Text("Ok") }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Livro") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Seção: Capa ──────────────────────────────────────────────────
            Text("Capa do Livro", style = MaterialTheme.typography.titleMedium)

            CoverPickerSection(
                selectedUri   = uiState.selectedImageUri,
                uploadedUrl   = uiState.uploadedImageUrl,
                isUploading   = uiState.isUploadingImage,
                onPickImage   = {
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveImage = onImageRemoved
            )

            // ── Seção: Informações ───────────────────────────────────────────
            Text("Informações do Livro", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value         = nome,
                onValueChange = {
                    nome = it
                    nomeError = if (it.isBlank()) "Título é obrigatório." else null
                },
                label         = { Text("Título *") },
                isError       = nomeError != null,
                supportingText = nomeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                enabled       = !isLoading
            )

            OutlinedTextField(
                value         = autor,
                onValueChange = { autor = it },
                label         = { Text("Autor") },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                enabled       = !isLoading
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = numPag,
                    onValueChange = {
                        numPag = it.filter { c -> c.isDigit() }
                        numPagError = if (numPag.isBlank()) "Obrigatório."
                        else if ((numPag.toIntOrNull() ?: 0) <= 0) "Valor inválido."
                        else null
                    },
                    label         = { Text("Páginas *") },
                    isError       = numPagError != null,
                    supportingText = numPagError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier      = Modifier.weight(1f),
                    enabled       = !isLoading
                )
                OutlinedTextField(
                    value         = ano,
                    onValueChange = { ano = it.filter { c -> c.isDigit() }.take(4) },
                    label         = { Text("Ano") },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier      = Modifier.weight(1f),
                    enabled       = !isLoading
                )
            }

            // ── Seção: Status ────────────────────────────────────────────────
            Text("Status de Leitura", style = MaterialTheme.typography.titleMedium)

            LazyRow(
                contentPadding = PaddingValues(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(BookStatus.entries) { option ->
                    FilterChip(
                        selected = status == option,
                        onClick  = { if (!isLoading) status = option },
                        label    = { Text(option.label) }
                    )
                }
            }

            // ── Seção: Descrição ─────────────────────────────────────────────
            Text("Descrição / Sinopse", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value         = descricao,
                onValueChange = { descricao = it },
                label         = { Text("Descrição") },
                minLines      = 4,
                maxLines      = 8,
                modifier      = Modifier.fillMaxWidth(),
                enabled       = !isLoading
            )

            Spacer(Modifier.height(8.dp))

            // ── Botão de submit ───────────────────────────────────────────────
            Button(
                onClick = {
                    // Validação final antes de enviar
                    nomeError   = if (nome.isBlank()) "Título é obrigatório." else null
                    numPagError = if (numPag.isBlank()) "Obrigatório."
                    else if ((numPag.toIntOrNull() ?: 0) <= 0) "Valor inválido."
                    else null

                    if (nomeError == null && numPagError == null) {
                        onSubmit(nome, autor, numPag, status.label, ano, descricao)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled  = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color     = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(if (uiState.isUploadingImage) "Enviando imagem..." else "Salvando livro...")
                } else {
                    Text("Cadastrar Livro")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─── Seção de seleção/preview de capa ─────────────────────────────────────────

@Composable
private fun CoverPickerSection(
    selectedUri: Uri?,
    uploadedUrl: String?,
    isUploading: Boolean,
    onPickImage: () -> Unit,
    onRemoveImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageToShow: Any? = uploadedUrl ?: selectedUri

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isUploading, onClick = onPickImage),
        contentAlignment = Alignment.Center
    ) {
        if (imageToShow != null) {
            // Preview da imagem selecionada
            AsyncImage(
                model              = imageToShow,
                contentDescription = "Preview da capa",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )

            // Botão remover (canto superior direito)
            if (!isUploading) {
                IconButton(
                    onClick  = onRemoveImage,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remover imagem",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Overlay de upload
            if (isUploading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            // Estado vazio: convida o usuário a selecionar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text  = "Toque para selecionar a capa",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text  = "(opcional)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (imageToShow != null && !isUploading) {
        OutlinedButton(
            onClick  = onPickImage,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trocar imagem")
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Cadastro – Vazio")
@Composable
private fun CreateBookEmptyPreview() {
    EntrelinhasTheme(darkTheme = false) {
        CreateBookContent(
            uiState          = CreateBookUiState(),
            onBackClick      = {},
            onImageSelected  = {},
            onImageRemoved   = {},
            onErrorDismissed = {},
            onSubmit         = { _, _, _, _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Cadastro – Salvando")
@Composable
private fun CreateBookLoadingPreview() {
    EntrelinhasTheme(darkTheme = false) {
        CreateBookContent(
            uiState          = CreateBookUiState(isSaving = true),
            onBackClick      = {},
            onImageSelected  = {},
            onImageRemoved   = {},
            onErrorDismissed = {},
            onSubmit         = { _, _, _, _, _, _ -> }
        )
    }
}