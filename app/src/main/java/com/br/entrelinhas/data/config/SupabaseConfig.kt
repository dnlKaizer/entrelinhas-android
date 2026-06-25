package com.br.entrelinhas.data.config

import com.br.entrelinhas.BuildConfig
import com.br.entrelinhas.R

object SupabaseConfig {
    val supabaseUrl: String = BuildConfig.SUPABASE_URL
    val supabaseKey: String = BuildConfig.SUPABASE_ANON_KEY
    val adminKey: String = BuildConfig.ADMIN_KEY

    fun getCoverUrl(img: String?): Any {
        return if (!img.isNullOrBlank()) {
            "$supabaseUrl/storage/v1/object/public/covers/$img"
        } else {
            R.drawable.book_placeholder
        }
    }
}
