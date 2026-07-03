package com.br.entrelinhas

import android.app.Application
import com.br.entrelinhas.data.local.AppDatabase
import com.br.entrelinhas.data.remote.BookRemoteDataSource
import com.br.entrelinhas.data.remote.StorageService
import com.br.entrelinhas.data.remote.SupabaseClientProvider
import com.br.entrelinhas.data.repository.BookRepository
import com.br.entrelinhas.ui.viewmodel.CreateBookViewModelFactory
import com.br.entrelinhas.ui.viewmodel.HomeViewModelFactory

/**
 * Application class que inicializa o grafo de dependências manualmente (sem Hilt).
 *
 * Todas as dependências são lazy: só são criadas na primeira vez que são acessadas,
 * evitando trabalho desnecessário na inicialização do app.
 *
 * Para usar nas telas:
 *   val app = LocalContext.current.applicationContext as EntrelinhasApplication
 *   val viewModel: HomeViewModel = viewModel(factory = app.homeViewModelFactory)
 */
class EntrelinhasApplication : Application() {

    // ── Camada remota ─────────────────────────────────────────────────────────
    private val supabaseClient by lazy { SupabaseClientProvider.client }

    val storageService: StorageService by lazy {
        StorageService(supabaseClient)
    }

    private val bookRemoteDataSource: BookRemoteDataSource by lazy {
        BookRemoteDataSource(supabaseClient)
    }

    // ── Camada local ──────────────────────────────────────────────────────────
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(this)
    }

    private val bookDao by lazy { database.bookDao() }

    // ── Repositório ───────────────────────────────────────────────────────────
    val bookRepository: BookRepository by lazy {
        BookRepository(bookRemoteDataSource, bookDao)
    }

    // ── Factories de ViewModel ────────────────────────────────────────────────
    val homeViewModelFactory: HomeViewModelFactory by lazy {
        HomeViewModelFactory(bookRepository)
    }

    val createBookViewModelFactory: CreateBookViewModelFactory by lazy {
        CreateBookViewModelFactory(this, bookRepository, storageService)
    }
}
