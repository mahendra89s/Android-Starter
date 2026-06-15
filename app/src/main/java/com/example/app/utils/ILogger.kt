package com.example.app.utils

interface ILogger {
    fun debug(tag: String, message: String)
    fun error(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable)
}