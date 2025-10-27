package com.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * Interface for providing location updates.
 */
interface LocationProvider {
    /**
     * Returns a [Flow] that emits location updates.
     *
     * @return A [Flow] of [Location] objects.
     */
    fun getLocationUpdates(): Flow<Location>

    /**
     * Returns the last known location.
     *
     * @return The last known [Location] object, or null if not available.
     */
    suspend fun getLastKnownLocation(): Location?

    /**
     * Returns a [Flow] that emits whether location services are enabled.
     *
     * @return A [Flow] of [Boolean] values.
     */
    fun isLocationEnabled(): Flow<Boolean>

    /**
     *  Returns true if the app has location permissions, false otherwise.
     */
    fun hasLocationPermissions(): Boolean

    /**
     * Returns the coordinates for a given city name.
     *
     * @param cityName The name of the city to get coordinates for.
     * @return A [Location] object representing the city's coordinates, or null if not found.
     */
    suspend fun getCoordinatesForCity(cityName: String): Location?
}