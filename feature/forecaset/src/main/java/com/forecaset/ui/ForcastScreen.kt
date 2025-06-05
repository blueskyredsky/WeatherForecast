package com.forecaset.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ForecastScreen(
    // This callback signals that Forecast needs to show a detail, but it doesn't know *how*
    // or where that detail screen actually lives.
    onNavigateToDetail: (locationId: String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forecast Home Screen")
        Button(
            onClick = { onNavigateToDetail("London") }, // Request to show detail for "London"
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("View London Weather Detail")
        }
        // Add more UI elements as needed
    }
}