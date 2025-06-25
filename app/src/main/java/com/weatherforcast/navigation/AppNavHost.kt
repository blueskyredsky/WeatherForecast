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
    startDestination: String = FORECAST_GRAPH_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        addForecastGraph(
            navController = navController,
            onNavigateOut = { callback ->
                when (callback) {
                    is ForecastNavCallback.NavigateToDetail -> {
                        navController.navigate(DetailRoutes.ItemDetail.createRoute(callback.locationName))
                    }
                }
            }
        )

        addDetailGraph(
            navController = navController,
            onNavigateOut = { callback ->
                when (callback) {
                    is DetailNavCallback.NavigateBackToForecastHome -> {
                        navController.navigate(FORECAST_GRAPH_ROUTE) {
                            popUpTo(FORECAST_GRAPH_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        )

        // other top-level app screens or other feature graphs
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