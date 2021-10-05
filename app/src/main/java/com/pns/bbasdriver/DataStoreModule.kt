package com.pns.bbasdriver

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreModule(private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "dataStore")
    private val userID = stringPreferencesKey("userID")
    private val userName = stringPreferencesKey("userName")
    private val userVerify = booleanPreferencesKey("userVerify")

    suspend fun setUserID(id: String) {
        context.dataStore.edit { preferences ->
            preferences[userID] = id
        }
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[userName] = name
        }
    }

    suspend fun setUserVerify(isVerify: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[userVerify] = isVerify
        }
    }

    val mUserID: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[userID] ?: ""
            }

    val mUserName: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[userName] ?: ""
            }

    val mUserVerify: Flow<Boolean> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[userVerify] ?: false
            }
}