package com.example.stadmin.core.crypto

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.stadmin.util.Constants
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val SECURE_PREFERENCE = "secure_preference"
private const val KEY = "access_key"

private val Context.dataStore by preferencesDataStore(name = SECURE_PREFERENCE)

class KeyManager(private val context: Context) {

    companion object {
        private val ACCESS_KEY = stringPreferencesKey(KEY)
    }

    suspend fun saveAccessKey(key: String): Boolean {
        return try {
            context.dataStore.edit { preferences ->
                preferences[ACCESS_KEY] = key
            }
            true
        } catch (e: Exception) {
            Log.e(Constants.TAG, "KeyManager saveAccessKey error: ${e.message}")
            false
        }
    }

    suspend fun getAccessKey(): String? {
        return try {
            context.dataStore.data.map { it[ACCESS_KEY] }.first()
        } catch (e: Exception) {
            Log.e(Constants.TAG, "KeyManager getAccessKey error: ${e.message}")
            null
        }
    }

    suspend fun hasAccessKey(): Boolean = getAccessKey() != null

    suspend fun clear(): Boolean {
        return try {
            context.dataStore.edit { it.clear() }
            true
        } catch (e: Exception) {
            Log.e(Constants.TAG, "KeyManager clear error: ${e.message}")
            false
        }
    }
}