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
    private val busRouteName = stringPreferencesKey("busRouteName")
    private val cityCode = stringPreferencesKey("cityCode")
    private val routeId = stringPreferencesKey("routeId")
    private val vehicleNo = stringPreferencesKey("vehicleNo")


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

    suspend fun setBusRouteName(id: String) {
        context.dataStore.edit { preferences ->
            preferences[busRouteName] = id
        }
    }

    suspend fun setUserVerify(isVerify: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[userVerify] = isVerify
        }
    }

    suspend fun setCityCode(number: String) {
        context.dataStore.edit { preferences ->
            preferences[cityCode] = number
        }
    }

    suspend fun setVihicleNo(number: String) {
        context.dataStore.edit { preferences ->
            preferences[vehicleNo] = number
        }
    }

    suspend fun setRouteId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[routeId] = id
        }
    }

    suspend fun delete() {
        context.dataStore.edit {
            it.clear()
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

    val mBusRouteName: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[busRouteName] ?: ""
            }

    val mCityCode: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[cityCode] ?: ""
            }

    val mRouteId: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[routeId] ?: ""
            }

    val mVehicleNo: Flow<String> =
        context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[vehicleNo] ?: ""
            }
}