package com.forecast.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.datastore.UserPreferenceManager
import com.notification.DEEP_LINK_LOCATION_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userPreferenceManager: UserPreferenceManager
) : ViewModel() {

    val deepLinkedNewsResource = savedStateHandle.getStateFlow<String?>(
        key = DEEP_LINK_LOCATION_KEY,
        null,
    )
}