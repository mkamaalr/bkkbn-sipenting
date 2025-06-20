package com.bkkbnjabar.sipenting.utils

/**
* Kelas generik yang menampung nilai dengan status loading-nya.
* @param <T>
*/
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}