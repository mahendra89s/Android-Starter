package com.example.app.utils

sealed interface Resource<out R, out E> {
    data class Success<out T>(val data: T): Resource<T, Nothing>
    data class Error<out E>(val error: E? = null): Resource<Nothing, E>
}