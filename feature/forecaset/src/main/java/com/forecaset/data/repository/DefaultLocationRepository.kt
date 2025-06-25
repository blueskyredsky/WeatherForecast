package com.forecaset.data.repository

import android.location.Location
import com.location.LocationProvider
import com.reza.threading.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class DefaultLocationRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {
    override fun getLocationUpdates(): Flow<Location> {
        return locationProvider.getLocationUpdates().flowOn(ioDispatcher)
    }

    override suspend fun getLastKnownLocation(): Location? {
        return locationProvider.getLastKnownLocation()
    }

    override fun isLocationEnabled(): Flow<Boolean> {
        return locationProvider.isLocationEnabled().flowOn(ioDispatcher)
    }

    override fun hasLocationPermissions(): Boolean {
        return locationProvider.hasLocationPermissions()
    }
}