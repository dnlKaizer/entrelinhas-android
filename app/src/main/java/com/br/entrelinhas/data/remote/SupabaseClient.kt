package com.br.entrelinhas.data.remote

import com.br.entrelinhas.data.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Singleton que fornece a instância única do cliente Supabase para todo o app.
 * Inicializado de forma lazy — só consome recursos quando primeiro acessado.
 */
object SupabaseClientProvider {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.SUPABASE_URL,
            supabaseKey = SupabaseConfig.SUPABASE_KEY
        ) {
            install(Postgrest)
            install(Storage)
            install(Auth)
        }
    }

    /**
     * Faz login com o usuário admin e mantém a sessão ativa.
     * Deve ser chamado uma vez, antes de qualquer requisição ao banco.
     * Se já estiver logado, não faz nada.
     */
    suspend fun ensureLoggedIn() {
        val session = client.auth.currentSessionOrNull()
        if (session != null) return  // já autenticado

        client.auth.signInWith(Email) {
            email    = SupabaseConfig.ADMIN_EMAIL
            password = SupabaseConfig.ADMIN_PASSWORD
        }
    }
}