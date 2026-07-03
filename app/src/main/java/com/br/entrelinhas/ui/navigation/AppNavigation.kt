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
import com.br.entrelinhas.ui.screens.CreateBookScreen
import com.br.entrelinhas.ui.screens.HomeScreen

private const val ARG_BOOK_ID = "bookId"

/**
 * Rotas do app.
 * Centralizar aqui evita strings mágicas espalhadas pelo código.
 */
private object Routes {
    const val HOME        = "home"
    const val BOOK_DETAILS = "book_details/{$ARG_BOOK_ID}"
    const val CREATE_BOOK = "create_book"

    fun bookDetails(bookId: Int) = "book_details/$bookId"
}

/**
 * Grafo de navegação completo:
 *
 *   Home ──(livro)──► Book Details
 *     └──(FAB)──────► Create Book ──(sucesso)──► Home (pop)
 *
 * [isDarkTheme] e [onToggleTheme] são mantidos em [MainActivity] (state hoisting)
 * e repassados para a Home através da navegação.
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController  = navController,
        startDestination = Routes.HOME,
        modifier       = modifier
    ) {
        composable(route = Routes.HOME) { backStackEntry ->
            HomeScreen(
                backStackEntry = backStackEntry,
                isDarkTheme   = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onBookClick   = { bookId ->
                    navController.navigate(Routes.bookDetails(bookId))
                },
                onCreateBook  = {
                    navController.navigate(Routes.CREATE_BOOK)
                }
            )
        }

        composable(
            route     = Routes.BOOK_DETAILS,
            arguments = listOf(navArgument(ARG_BOOK_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt(ARG_BOOK_ID) ?: -1
            BookDetailsScreen(
                bookId      = bookId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Routes.CREATE_BOOK) {
            CreateBookScreen(
                onBackClick   = { navController.popBackStack() },
                onBookCreated = {
                    // Volta para a Home e sinaliza para recarregar a lista
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("book_created", true)
                    navController.popBackStack()
                }
            )
        }
    }
}