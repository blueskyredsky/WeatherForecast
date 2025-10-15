package com.currentweather.data.repository

import android.location.Location
import com.location.LocationProvider
import com.reza.threading.common.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultLocationRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {
    override fun getLocationUpdates(): Flow<Location> {
        return locationProvider.getLocationUpdates().flowOn(ioDispatcher)
    }

    override suspend fun getLastKnownLocation(): Location? = withContext(ioDispatcher) {
        locationProvider.getLastKnownLocation()
    }

    override fun isLocationEnabled(): Flow<Boolean> {
        return locationProvider.isLocationEnabled().flowOn(ioDispatcher)
    }

    override fun hasLocationPermissions(): Boolean {
        return locationProvider.hasLocationPermissions()
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): String? {
        return locationProvider.getCityName(latitude = latitude, longitude = longitude)
    }
}