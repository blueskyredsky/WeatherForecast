package com.currentweather.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for accessing location data.
 */
interface LocationRepository {
    /**
     * Returns a flow that emits location updates.
     */
    fun getLocationUpdates(): Flow<Location>

    /**
     * Returns the last known location.
     */
    suspend fun getLastKnownLocation(): Location?

    /**
     * Returns a flow that emits whether location services are enabled.
     */
    fun isLocationEnabled(): Flow<Boolean>

    /**
     * Returns whether the app has location permissions.
     */
    fun hasLocationPermissions(): Boolean

    /**
     * Returns the name of the city based on the provided latitude and longitude.
     */
    suspend fun getCityName(latitude: Double, longitude: Double): String?
}