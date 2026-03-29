package com.example.app.model.network

import kotlinx.serialization.Serializable

@Serializable
data class ArticleRM(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceRM?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)
