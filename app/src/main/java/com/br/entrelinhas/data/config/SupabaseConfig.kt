package com.br.entrelinhas.data.config

import com.br.entrelinhas.BuildConfig

object SupabaseConfig {
    const val SUPABASE_URL: String = BuildConfig.SUPABASE_URL
    const val SUPABASE_KEY: String = BuildConfig.SUPABASE_ANON_KEY
    const val ADMIN_KEY: String = BuildConfig.ADMIN_KEY

    fun getCoverUrl(img: String?): Any {
        return if (!img.isNullOrBlank()) {
            "$supabaseUrl/storage/v1/object/public/covers/$img"
        } else {
            R.drawable.book_placeholder
        }
    }
}
