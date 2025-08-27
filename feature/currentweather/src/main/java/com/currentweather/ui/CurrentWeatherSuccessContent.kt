package com.currentweather.ui

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.currentweather.R
import com.currentweather.data.model.currentweather.CurrentWeather

@Composable
fun SuccessContent(
    currentWeather: CurrentWeather?,
    weatherData: WeatherUI,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (cityName: String) -> Unit
) {
    val view = LocalView.current
    val barColor = colorResource(weatherData.backgroundColorResource)

    SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = barColor.toArgb()
        window.navigationBarColor = barColor.toArgb()

        WindowCompat.getInsetsController(window, view).apply {
            val isLight = barColor.luminance() > 0.5f
            isAppearanceLightStatusBars = isLight
            isAppearanceLightNavigationBars = isLight
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(weatherData.backgroundColorResource))
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            painter = painterResource(weatherData.backgroundImageResource),
            contentDescription = null, // decorative image
        )

        currentWeather?.let { weather ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                Text(
                    text = stringResource(R.string.today),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${weather.current?.tempC ?: ""}°",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
                )

                Spacer(modifier = Modifier.height(32.dp))

                weather.current?.condition?.icon?.let { iconUrl ->
                    AsyncImage(
                        model = "https:$iconUrl",
                        contentDescription = stringResource(R.string.weather_condition_icon),
                        modifier = Modifier.size(64.dp)
                    )
                }

                Text(
                    textAlign = TextAlign.Center,
                    text = weather.current?.condition?.text.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                weather.location?.name?.let { cityName ->
                    Button(
                        onClick = { onNavigateToDetail(cityName) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.view_detail, cityName)
                        )
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.no_weather_data_available)
                )
            }
        }
    }
}