package com.example.connect.utils


sealed class State<T> {
    class Loading<T>(val data: T? = null) : State<T>()
    data class Error<T>(val errorMessage: String?, val error: Throwable, val data: T? = null) :
        State<T>()

    data class Success<T>(var data: T) : State<T>()

    companion object {
        fun <T> loading(data: T? = null): State<T> = Loading(data)

        fun <T> error(errorMessage: String, error: Throwable, data: T? = null): State<T> =
            Error(errorMessage, error, data)

        fun <T> success(data: T): State<T> = Success(data)
    }
}