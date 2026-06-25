package com.br.entrelinhas.data.remote

import com.br.entrelinhas.data.config.SupabaseConfig
import io.github.jan.supabase.SupabaseClient
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
            supabaseUrl = SupabaseConfig.supabaseUrl,
            supabaseKey = SupabaseConfig.supabaseKey
        ) {
            install(Postgrest)
            install(Storage)
        }
    }
}