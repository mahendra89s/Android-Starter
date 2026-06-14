package com.example.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject

class AppDataStore @Inject constructor(
    context: Context
): AbstractPreferenceDataStore(PREF_NAME,context) {

    suspend fun saveUserNameKey(value: String){
        save(USER_NAME_KEY, value)
    }

    suspend fun getUserNameKey(): String? = get(USER_NAME_KEY)

    companion object {
        private const val PREF_NAME = "user_preferences"
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }
}