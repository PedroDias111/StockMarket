package com.example.stockmarket.util

//Class to Handle results and state from API request
sealed class Resource<T> (val data: T? = null, val message: String? = null){
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
    class Loading<T>(val isLoading: Boolean): Resource<T>(null)
}