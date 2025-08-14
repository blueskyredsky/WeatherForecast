package com.forecast.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.forecast.R

@Composable
fun ForecastScreen(
    locationName: String,
    onGoBackToForecast: () -> Unit // Callback to signal navigation back to Forecast
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.forecast_screen_for, locationName))
        Button(
            onClick = onGoBackToForecast,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go back to Forecast Home")
        }
    }
}