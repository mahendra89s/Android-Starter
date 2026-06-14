package com.example.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.getValue

abstract class AbstractPreferenceDataStore(
    prefName: String,
    context: Context
) {
    private val dataStore by lazy {
        PreferenceDataStoreFactory.create (
            produceFile = {
                context.preferencesDataStoreFile(prefName)
            }
        )
    }

    protected suspend fun <T>save(
        key: Preferences.Key<T>,
        value: T
    ) {
        dataStore.edit {
            it[key] = value
        }
    }


    protected suspend fun <T>get(key: Preferences.Key<T>): T? {
        val preferences = dataStore.data.first {
            it.contains(key)
        }
        return preferences[key]
    }

    protected suspend fun <T> remove(key: Preferences.Key<T>) {
        withContext(Dispatchers.IO){
            dataStore.edit {
                it.remove(key)
            }
        }
    }

    protected suspend fun <T> getAsync(key: Preferences.Key<T>): Flow<T?> = dataStore.data.catch {
        emit(
            emptyPreferences()
        )
    }.map {
        it[key]
    }

    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }

}