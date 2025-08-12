package com.common.model.error

sealed class RepositoryError : Throwable() {
    data class NetworkError(val code: Int, override val message: String? = null) : RepositoryError()
    data class MappingError(override val cause: Throwable) : RepositoryError()
    data class UnknownError(override val cause: Throwable? = null) : RepositoryError()
}