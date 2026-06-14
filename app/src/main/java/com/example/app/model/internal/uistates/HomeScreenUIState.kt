package com.example.app.model.internal.uistates

import com.example.app.model.network.ArticleRM

data class HomeScreenUIState (
    val articles: List<ArticleRM> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)
