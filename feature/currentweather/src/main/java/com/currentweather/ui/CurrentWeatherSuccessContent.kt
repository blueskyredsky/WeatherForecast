package com.currentweather.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.common.model.extension.toFormattedTime
import com.currentweather.R
import com.currentweather.data.model.currentweather.CurrentWeather
import com.currentweather.data.model.forecast.Forecast
import com.currentweather.ui.component.ConnectedWavyLines
import java.util.Calendar

@Composable
fun SuccessContent(
    currentWeather: CurrentWeather?,
    forecast: Forecast?,
    weatherData: WeatherUI,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (cityName: String) -> Unit
) {
    val color = colorResource(weatherData.textColorResource)
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
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                Text(
                    text = stringResource(R.string.today),
                    style = MaterialTheme.typography.titleLarge,
                    color = color
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${weather.current.tempC}°",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 100.sp),
                    color = color
                )

                Spacer(modifier = Modifier.height(100.dp))

                weather.location.name.let { cityName ->
                    Icon(
                        painter = painterResource(com.common.R.drawable.ic_loaction),
                        contentDescription = null,
                        tint = color
                    )

                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.displayLarge,
                        color = color
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                ConnectedWavyLines(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = color
                )

                forecast?.forecast?.forecastDay?.first()?.hour?.let { hours ->
                    val listState = rememberLazyListState()
                    LazyRow(state = listState) {
                        items(hours, key = { it.time.hashCode() }) {
                            ItemHourlyForecast(
                                iconUrl = it.condition.icon,
                                temperature = it.tempC.toString(),
                                time = it.time,
                                color = color
                            )
                        }
                    }

                    LaunchedEffect(key1 = hours) {
                        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        val targetIndex = hours.indexOfFirst {
                            it.time.substringAfter(" ").substringBefore(":").toInt() == currentHour
                        }
                        if (targetIndex != -1) {
                            listState.scrollToItem(targetIndex) // scroll to current time
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                weather.location.name.let { cityName ->
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

@Composable
private fun ItemHourlyForecast(
    iconUrl: String,
    temperature: String,
    time: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = time.toFormattedTime(),
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
        AsyncImage(
            model = "https:$iconUrl",
            contentDescription = stringResource(R.string.weather_condition_icon),
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = "$temperature°",
            style = MaterialTheme.typography.titleSmall,
            color = color
        )
    }
}