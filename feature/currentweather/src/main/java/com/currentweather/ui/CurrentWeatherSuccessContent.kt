package com.currentweather.ui

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
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

                Spacer(modifier = Modifier.height(100.dp))

                weather.location?.name?.let { cityName ->
                    Icon(
                        painter = painterResource(com.common.R.drawable.ic_loaction),
                        contentDescription = null
                    )

                    Text(
                        text = cityName,
                        style = MaterialTheme.typography.displayLarge
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                ConnectedWavyLines(modifier = Modifier.padding(8.dp))

                /*weather.current?.condition?.icon?.let { iconUrl ->
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
                )*/

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

@Composable
private fun ConnectedWavyLines(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val circleRadius = 8.dp.toPx()
        val lineGap = 2.dp.toPx()

        val lineEndLeft = centerX - circleRadius - lineGap
        val lineStartRight = centerX + circleRadius + lineGap

        // Left Wavy Line with Gradient
        val pathLeft = Path().apply {
            moveTo(0f, centerY)
            quadraticTo(width * 0.2f, centerY * 0.7f, lineEndLeft, centerY)
        }
        drawPath(
            path = pathLeft,
            brush = Brush.horizontalGradient(
                colors = listOf(Color.LightGray, Color.DarkGray),
                startX = 0f,
                endX = lineEndLeft
            ),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Right Wavy Line
        val pathRight = Path().apply {
            moveTo(lineStartRight, centerY)
            cubicTo(
                width * 0.6f, centerY * 1.25f,
                width * 0.8f, centerY * 0.75f,
                width, centerY
            )
        }
        drawPath(
            path = pathRight,
            color = Color.Black,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Connecting Circle
        drawCircle(
            color = Color.Black,
            radius = circleRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
        )

        // Vertical Line
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY + circleRadius),
            end = Offset(centerX, centerY + circleRadius + 32.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}