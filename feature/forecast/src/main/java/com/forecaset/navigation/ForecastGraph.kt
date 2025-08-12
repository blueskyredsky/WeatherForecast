package com.forecaset.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.forecaset.ui.ForecastScreen

/**
 * The root route for the entire Forecast feature graph.
 * This is what the app module will use to navigate *into* the Forecast feature.
 */
const val FORECAST_GRAPH_ROUTE = "forecast_graph"

/**
 * Extension function on NavGraphBuilder to add the Forecast feature's navigation graph.
 * This is called from the main App NavHost in the :app module.
 *
 * @param navController The NavController from the App module. It's passed here mainly
 * for potential future internal sub-navigation if the Forecast module grows.
 * @param onNavigateOut The callback for actions that require navigation outside the Forecast feature.
 * The Forecast UI screens will invoke this when they need to signal
 * a navigation event to the App module (e.g., "go to the Detail feature").
 */
fun NavGraphBuilder.addForecastGraph(
    navController: NavController,
    onNavigateOut: (ForecastNavCallback) -> Unit
) {
    navigation(
        startDestination = ForecastRoutes.ForecastHome.route,
        route = FORECAST_GRAPH_ROUTE
    ) {
        composable(ForecastRoutes.ForecastHome.route) { backStackEntry ->
            ForecastScreen(
                onNavigateToDetail = { locationName ->
                    // This is NOT an internal navigation. It's an external navigation request.
                    // The Forecast module requests the App module to handle navigation to detail.
                    onNavigateOut(ForecastNavCallback.NavigateToDetail(locationName))
                }
            )
        }
    }
}