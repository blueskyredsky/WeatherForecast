package com.detail.navigation

sealed class DetailRoutes(val route: String) {
    data object ItemDetail : DetailRoutes("item_detail_screen/{itemId}") {
        fun createRoute(itemId: String) = "item_detail_screen/$itemId"
    }
}

sealed interface DetailNavCallback {
    data object NavigateBackToForecastHome : DetailNavCallback
}