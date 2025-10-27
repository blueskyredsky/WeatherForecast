package com.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.reza.threading.common.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationProvider {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (!hasLocationPermissions()) {
            close(Exception("Location permissions not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(10_000L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(5_000L)
            .setMaxUpdateDelayMillis(15_000L)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermissions()) {
            throw Exception("Location permissions not granted")
        }

        return try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }

    override fun isLocationEnabled(): Flow<Boolean> {
        return flowOf(
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        )
    }

    override fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override suspend fun getCoordinatesForCity(cityName: String): Location? =
        withContext(ioDispatcher) {
            val geocoder = Geocoder(context, Locale.getDefault())

            return@withContext try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    getFromLocationNameApi33(geocoder, cityName)
                } else {
                    getFromLocationNameLegacy(geocoder, cityName)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getFromLocationNameApi33(
        geocoder: Geocoder,
        locationName: String
    ): Location? =
        suspendCancellableCoroutine { continuation ->
            val listener = object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: List<Address?>) {
                    if (continuation.isActive) {
                        val result = if (addresses.isNotEmpty()) {
                            addresses.firstNotNullOfOrNull { address ->
                                address?.let {
                                    Location("geocoder").apply {
                                        latitude = it.latitude
                                        longitude = it.longitude
                                    }
                                }
                            }
                        } else {
                            null
                        }
                        continuation.resume(result)
                    }
                }

                override fun onError(errorMessage: String?) {
                    if (continuation.isActive) {
                        Log.e("Geocoder", "Forward geocoding error: $errorMessage")
                        continuation.resume(null)
                    }
                }
            }
            // Request up to 5 results for better chance of finding a match
            geocoder.getFromLocationName(locationName, 5, listener)
        }

    private fun getFromLocationNameLegacy(
        geocoder: Geocoder,
        locationName: String
    ): Location? {
        return try {
            // Request up to 5 results
            val addresses = geocoder.getFromLocationName(locationName, 5)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Location("geocoder").apply {
                    latitude = address.latitude
                    longitude = address.longitude
                }
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }
}