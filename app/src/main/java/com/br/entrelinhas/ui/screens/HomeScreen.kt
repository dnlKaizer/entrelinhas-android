package com.br.entrelinhas.ui.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.br.entrelinhas.EntrelinhasApplication
import com.br.entrelinhas.ui.components.HomeContent
import com.br.entrelinhas.ui.viewmodel.HomeViewModel
import com.br.entrelinhas.ui.viewmodel.HomeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    backStackEntry: NavBackStackEntry,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBookClick: (Int) -> Unit,
    onCreateBook: () -> Unit,
    modifier: Modifier = Modifier
) {
    val app = LocalContext.current.applicationContext as EntrelinhasApplication
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(app.bookRepository))

    val savedStateHandle = backStackEntry.savedStateHandle
    val bookCreated by savedStateHandle
        .getStateFlow("book_created", false)
        .collectAsState()

    LaunchedEffect(bookCreated) {
        if (bookCreated) {
            viewModel.refresh()
            savedStateHandle["book_created"] = false
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    HomeContent(
        uiState       = uiState,
        isDarkTheme   = isDarkTheme,
        onToggleTheme = onToggleTheme,
        onBookClick   = onBookClick,
        onCreateBook  = onCreateBook,
        onRetry       = { viewModel.loadBooks() },
        onRefresh     = { viewModel.refresh() },
        modifier      = modifier
    )
}