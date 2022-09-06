package com.sample.quotesapp.data.models

/**
 * Class to handle API responses
 */
sealed class Resource<out T : Any> {
    data class Success<out T : Any>(val data: T?) : Resource<T>()
    data class Error<out T : Any>(val exception: Throwable) : Resource<T>()
}