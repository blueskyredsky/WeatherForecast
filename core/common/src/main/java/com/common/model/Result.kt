package com.common.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val errorType: ErrorType = ErrorType.UNKNOWN) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

sealed interface ErrorType {
    data object LOCATION_SERVICES_DISABLED : ErrorType
    data object LOCATION_PERMISSIONS_DENIED : ErrorType
    data object UNKNOWN : ErrorType
}