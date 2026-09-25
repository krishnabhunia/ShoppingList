package com.krishna.shoppinglist.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val SHOW_SPLASH = booleanPreferencesKey("show_splash")
        val ALLOW_EDIT = booleanPreferencesKey("allow_edit")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { it[DARK_MODE] ?: false }
    val showSplash: Flow<Boolean> = context.dataStore.data.map { it[SHOW_SPLASH] ?: true }
    val allowEdit: Flow<Boolean> = context.dataStore.data.map { it[ALLOW_EDIT] ?: true }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = value }
    }

    suspend fun setShowSplash(value: Boolean) {
        context.dataStore.edit { it[SHOW_SPLASH] = value }
    }

    suspend fun setAllowEdit(value: Boolean) {
        context.dataStore.edit { it[ALLOW_EDIT] = value }
    }
}
