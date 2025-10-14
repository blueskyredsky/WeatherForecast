package com.datastore

import kotlinx.coroutines.flow.Flow

interface UserPreferenceManager {
    /**
     * Provides a Flow of the user's saved location.
     * Emits null if no location is set.
     */
    val userLocationFlow: Flow<String?>

    /**
     * Suspends and saves the user's location string.
     */
    suspend fun saveUserLocation(location: String)
}