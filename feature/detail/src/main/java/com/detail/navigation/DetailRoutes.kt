package com.detail.navigation

// Define routes specific to the detail feature
sealed class DetailRoutes(val route: String) {
    // This is the screen that shows the actual detail of a forecast (or any item)
    data object ItemDetail : DetailRoutes("item_detail_screen/{itemId}") {
        fun createRoute(itemId: String) = "item_detail_screen/$itemId"
    }
    // If your detail module had more screens, they would go here
}

// Define actions that the Detail feature needs the App module to handle.
sealed interface DetailNavCallback {
    // This action means "I'm done here, navigate back to the Forecast home screen."
    data object NavigateBackToForecastHome : DetailNavCallback
    // Add other actions specific to detail module that need app's intervention
}