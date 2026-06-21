package com.br.entrelinhas.data.config

import com.br.entrelinhas.BuildConfig

object SupabaseConfig {
    val supabaseUrl: String = BuildConfig.SUPABASE_URL
    val supabaseKey: String = BuildConfig.SUPABASE_ANON_KEY

    fun getCoverUrl(img: String): String {
        return "$supabaseUrl/storage/v1/object/public/covers/$img"
    }
}
