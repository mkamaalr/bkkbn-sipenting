package com.bkkbnjabar.sipenting.utils

/**
 * A generic sealed class that contains data and status about loading this data.
 * It's used to communicate states between the data layer and the UI layer.
 * @param T The type of the data being handled.
 */
sealed class Resource<out T> {
    /**
     * Represents a successful state with data.
     * @param data The data of type T that was successfully retrieved.
     */
    data class Success<out T>(val data: T) : Resource<T>()

    /**
     * Represents an error state with an error message.
     * @param message A string describing the error.
     */
    data class Error(val message: String) : Resource<Nothing>()

    /**
     * Represents the loading state.
     */
    object Loading : Resource<Nothing>()

    /**
     * Represents the initial, idle state before any operation has started.
     */
    object Idle : Resource<Nothing>()
}
