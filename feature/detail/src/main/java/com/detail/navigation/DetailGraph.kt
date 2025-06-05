package com.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.detail.ui.DetailScreen

/**
 * The root route for the entire Detail feature graph.
 * This is what the app module will use to navigate *into* the Detail feature.
 */
const val DETAIL_GRAPH_ROUTE = "detail_graph"

/**
 * Extension function on NavGraphBuilder to add the Detail feature's navigation graph.
 * This is called from the main App NavHost.
 *
 * @param navController The NavController from the App module.
 * @param onNavigateOut The callback for actions that require navigation outside the Detail feature.
 */
fun NavGraphBuilder.addDetailGraph(
    navController: NavController,
    onNavigateOut: (DetailNavCallback) -> Unit
) {
    navigation(
        startDestination = DetailRoutes.ItemDetail.route,
        route = DETAIL_GRAPH_ROUTE // The root route for this feature's graph
    ) {
        composable(
            route = DetailRoutes.ItemDetail.route,
            arguments = listOf(
                androidx.navigation.navArgument("itemId") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId")
            DetailScreen(
                itemId = itemId ?: "N/A",
                onGoBackToForecast = {
                    // This is an external navigation request.
                    // The Detail module requests the App module to handle navigation back to Forecast.
                    onNavigateOut(DetailNavCallback.NavigateBackToForecastHome)
                }
            )
        }
        // Add other composable screens for the detail feature here if any
    }
}