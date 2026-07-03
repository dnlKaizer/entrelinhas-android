package com.br.entrelinhas.data.config

import com.br.entrelinhas.BuildConfig

object SupabaseConfig {
    const val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    const val SUPABASE_KEY: String = BuildConfig.SUPABASE_ANON_KEY
    const val ADMIN_KEY: String = BuildConfig.ADMIN_KEY

    const val ADMIN_EMAIL: String = BuildConfig.ADMIN_EMAIL
    const val ADMIN_PASSWORD: String = BuildConfig.ADMIN_PASSWORD

    const val TABLE_LIVRO = "Livro"
    const val BUCKET_COVERS = "covers"
}
