package com.br.entrelinhas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.br.entrelinhas.ui.screens.BookDetailsScreen
import com.br.entrelinhas.ui.screens.HomeScreen

private const val ARG_BOOK_ID = "bookId"

/** Rotas do app, no formato esperado pelo Navigation Compose (navController.navigate("rota")). */
private object Routes {
    const val HOME = "home"
    const val BOOK_DETAILS = "book_details/{$ARG_BOOK_ID}"
    fun bookDetails(bookId: Int) = "book_details/$bookId"
}

/**
 * Grafo de navegação do app:
 *
 *   Home
 *     ↓
 *   Book Details
 *
 * [isDarkTheme] e [onToggleTheme] são repassados à Home para alternar o tema do app
 * (estado mantido em MainActivity, fora do NavHost).
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(route = Routes.HOME) {
            HomeScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onBookClick = { bookId ->
                    navController.navigate(Routes.bookDetails(bookId))
                }
            )
        }

        composable(
            route = Routes.BOOK_DETAILS,
            arguments = listOf(navArgument(ARG_BOOK_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt(ARG_BOOK_ID) ?: -1
            BookDetailsScreen(
                bookId = bookId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
