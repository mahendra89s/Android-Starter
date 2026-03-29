package com.example.app.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class DataStore @Inject constructor(
    val dataStore: DataStore<Preferences>
) {

    suspend fun save(
        key: String,
        value: String
    ) {
        dataStore.edit {
            it[USER_NAME_KEY] = value
        }
    }

    suspend fun get(key: String): String? {
        val preferences = dataStore.data.filter {
            it.contains(stringPreferencesKey(key))
        }.first()
        return preferences[USER_NAME_KEY]
    }

    companion object {
        val USER_NAME_KEY = stringPreferencesKey("user_name")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

}