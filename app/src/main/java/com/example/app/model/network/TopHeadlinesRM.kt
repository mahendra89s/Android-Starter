package com.example.app.model.network

import kotlinx.serialization.Serializable

@Serializable
data class TopHeadlinesRM(
    val articles: List<ArticleRM>?,
    val status: String?,
    val totalResults: Int?,
    val code: String? = null,
    val message: String? = null,
)
