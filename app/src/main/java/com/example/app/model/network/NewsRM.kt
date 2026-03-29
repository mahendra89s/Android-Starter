package com.example.app.model.network

import kotlinx.serialization.Serializable


@Serializable
data class NewsRM(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleRM>
)