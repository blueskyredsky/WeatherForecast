package com.currentweather.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.currentweather.ui.CurrentWeatherScreen
import com.currentweather.ui.CurrentWeatherViewModel

/**
 * The root route for the entire CurrentWeather feature graph.
 * This is what the app module will use to navigate *into* the CurrentWeather feature.
 */
const val CURRENT_WEATHER_GRAPH_ROUTE = "current_weather_graph"

/**
 * Extension function on NavGraphBuilder to add the CurrentWeather feature's navigation graph.
 * This is called from the main App NavHost in the :app module.
 *
 * @param navController The NavController from the App module. It's passed here mainly
 * for potential future internal sub-navigation if the CurrentWeather module grows.
 * @param onNavigateOut The callback for actions that require navigation outside the CurrentWeather feature.
 * The CurrentWeather UI screens will invoke this when they need to signal
 * a navigation event to the App module (e.g., "go to the Detail feature").
 */
fun NavGraphBuilder.addCurrentWeatherGraph(
    navController: NavController,
    onNavigateOut: (CurrentWeatherNavCallback) -> Unit
) {
    navigation(
        startDestination = CurrentWeatherRoutes.CurrentWeatherHome.route,
        route = CURRENT_WEATHER_GRAPH_ROUTE
    ) {
        composable(CurrentWeatherRoutes.CurrentWeatherHome.route) { backStackEntry ->
            // Use hiltViewModel from the navigation's parent graph to avoid viewModel recreation when navigating back
            // to this screen
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(CURRENT_WEATHER_GRAPH_ROUTE)
            }
            val viewModel: CurrentWeatherViewModel = hiltViewModel(parentEntry)
            CurrentWeatherScreen(
                viewModel = viewModel,
                onNavigateToDetail = { locationName ->
                    // This is NOT an internal navigation. It's an external navigation request.
                    // The current weather module requests the App module to handle navigation to detail.
                    onNavigateOut(CurrentWeatherNavCallback.NavigateToDetail(locationName))
                }
            )
        }
    }
}