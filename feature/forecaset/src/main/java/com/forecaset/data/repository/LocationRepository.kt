package com.forecaset.data.repository

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
     * Returns a flow that emits the last known location, or null if not available.
     */
    fun getLastKnownLocation(): Flow<Location?>

    /**
     * Returns a flow that emits whether location services are enabled.
     */
    fun isLocationEnabled(): Flow<Boolean>

    /**
     * Returns whether the app has location permissions.
     */
    fun hasLocationPermissions(): Boolean
}