package com.weatherforcast.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.detail.navigation.DetailNavCallback
import com.detail.navigation.DetailRoutes
import com.detail.navigation.addDetailGraph
import com.forecaset.navigation.FORECAST_GRAPH_ROUTE
import com.forecaset.navigation.ForecastNavCallback
import com.forecaset.navigation.addForecastGraph

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    // Set your app's initial start destination to the root of the Forecast feature graph
    startDestination: String = FORECAST_GRAPH_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Integrate the Forecast feature's navigation graph
        addForecastGraph(
            navController = navController, // Pass navController for internal Forecast navigation
            onNavigateOut = { callback ->
                when (callback) {
                    is ForecastNavCallback.NavigateToDetail -> {
                        // Handle navigation from Forecast to Detail feature.
                        // Navigate to the specific ItemDetail screen within the :detail module.
                        navController.navigate(DetailRoutes.ItemDetail.createRoute(callback.locationId))
                    }
                    // Handle other forecast-specific actions if any
                }
            }
        )

        // Integrate the Detail feature's navigation graph
        addDetailGraph(
            navController = navController, // Pass navController for internal Detail navigation
            onNavigateOut = { callback ->
                when (callback) {
                    is DetailNavCallback.NavigateBackToForecastHome -> {
                        // Handle navigation from Detail back to Forecast home.
                        // Navigate to the root of the Forecast feature graph.
                        navController.navigate(FORECAST_GRAPH_ROUTE) {
                            // Optional: Pop up to the forecast graph root to clear detail screens.
                            // If you want a consistent back stack where "Back" from Forecast home exits the app,
                            // you might use popUpTo(navController.graph.startDestinationId)
                            popUpTo(FORECAST_GRAPH_ROUTE) { inclusive = true } // Clear detail screens
                            launchSingleTop = true // Avoid multiple copies of Forecast home
                        }
                    }
                    // Handle other detail-specific actions if any
                }
            }
        )

        // You can add other top-level app screens or other feature graphs here
    }
}

/**
 * Extension functions on NavController for navigating to the root of feature graphs.
 * These are defined in the app module because the app module knows about all top-level
 * feature graphs and their root routes.
 */
fun NavController.navigateToForecastGraph() {
    navigate(FORECAST_GRAPH_ROUTE) {
        // Common navigation options for top-level tabs/features
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToDetailGraph(itemId: String) {
    navigate(DetailRoutes.ItemDetail.createRoute(itemId)) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}