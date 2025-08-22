package com.currentweather.ui

import androidx.compose.runtime.Composable
import com.common.R

@Composable
fun getBackgroundResource(text: String): Int {
    return when (text.lowercase()) {
        "sunny" -> R.drawable.ic_sunny
        "cloudy" -> R.drawable.ic_cloudy
        "rainy" -> R.drawable.ic_rainy
        else -> R.drawable.ic_cloudy // A fallback image
    }
}