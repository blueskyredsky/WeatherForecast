package com.forecaset.data.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(): Flow<Location>
    fun getLastKnownLocation(): Flow<Location?>
    fun isLocationEnabled(): Flow<Boolean>
    fun hasLocationPermissions(): Boolean
}