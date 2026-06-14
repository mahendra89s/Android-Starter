package com.example.app.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class HomeDetails(
    val articleName: String
)
