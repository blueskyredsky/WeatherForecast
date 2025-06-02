package com.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationUpdates(): Flow<Location>
    fun getLastKnownLocation(): Flow<Location?>
    fun isLocationEnabled(): Flow<Boolean>
    fun hasLocationPermissions(): Boolean
}