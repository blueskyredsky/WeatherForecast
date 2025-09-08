package com.common.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val errorType: ErrorType = ErrorType.UnknownError) :
        Result<Nothing>()

    data object Loading : Result<Nothing>()
}

sealed interface ErrorType {
    data object LocationServicesDisabled : ErrorType
    data object NetworkError : ErrorType
    data object NoDataError : ErrorType
    data object MappingError : ErrorType
    data object LocationPermissionDenied : ErrorType
    data object UnknownError : ErrorType
}