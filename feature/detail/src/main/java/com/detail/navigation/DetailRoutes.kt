package com.detail.navigation

sealed class DetailRoutes(val route: String) {
    data object ItemDetail : DetailRoutes("item_detail_screen/{locationName}") {
        fun createRoute(locationName: String) = "item_detail_screen/$locationName"
    }
}

sealed interface DetailNavCallback {
    data object NavigateBackToForecastHome : DetailNavCallback
}