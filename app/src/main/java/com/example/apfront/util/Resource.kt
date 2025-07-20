package com.example.apfront.util

// A generic class to handle states for network calls
sealed class Resource<T>(val data: T? = null, val message: String? = null, val code: Int? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, code: Int? = null) : Resource<T>(null, message,code)
    class Loading<T> : Resource<T>()
    class Idle<T> : Resource<T>()
}