package com.example.stadmin.core.supabase

import com.example.stadmin.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL ?: "",
        supabaseKey = BuildConfig.SUPABASE_KEY ?: "",
    ) {
        install(Postgrest)
        install(Storage)
    }

    fun from(table: SupabaseTable) = client.from(table.value)
}