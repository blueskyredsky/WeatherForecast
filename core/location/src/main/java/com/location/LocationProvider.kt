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
     * Returns a [Flow] that emits the last known location.
     *
     * @return A [Flow] of nullable [Location] objects.
     */
    fun getLastKnownLocation(): Flow<Location?>

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
}