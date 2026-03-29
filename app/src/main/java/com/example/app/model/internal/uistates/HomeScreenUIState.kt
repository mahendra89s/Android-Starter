package com.example.app.model.internal.uistates

import com.example.app.model.network.ArticleRM
import com.example.app.model.network.TopHeadlinesRM

sealed interface HomeScreenUIState {
    data class Success(val articles: List<ArticleRM>) : HomeScreenUIState
    data object Loading : HomeScreenUIState
    data class Error(val error: String) : HomeScreenUIState
}
