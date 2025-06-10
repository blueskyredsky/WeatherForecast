package com.forecaset.ui

import androidx.lifecycle.ViewModel
import com.forecaset.data.repository.ForecastRepository
import com.forecaset.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val forecastRepository: ForecastRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

}