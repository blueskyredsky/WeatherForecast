package com.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Delegation property for DataStore instance (file name)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

// Preference Key
private val USER_LOCATION_KEY = stringPreferencesKey("user_location")

@Singleton
class DataStoreUserPreferenceManager @Inject constructor(
    private val context: Context,
) : UserPreferenceManager {

    private val dataStore = context.dataStore

    override val userLocationFlow: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_LOCATION_KEY]
        }

    override suspend fun saveUserLocation(location: String) {
        dataStore.edit { preferences ->
            preferences[USER_LOCATION_KEY] = location
        }
    }
}