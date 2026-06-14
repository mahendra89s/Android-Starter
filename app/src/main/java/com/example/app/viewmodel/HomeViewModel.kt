package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.app.data.datastore.AppDataStore
import com.example.app.data.db.dao.ArticleDao
import com.example.app.data.db.entities.ArticleEntity
import com.example.app.data.remote.repo.INewsRepo
import com.example.app.model.internal.uistates.HomeScreenUIState
import com.example.app.pagingsource.PagingDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepo: INewsRepo,
    private val articleDao: ArticleDao,
    private val pagingDataSource: PagingDataSource,
    private val appDataStore: AppDataStore
): ViewModel() {
    private val _state: MutableStateFlow<HomeScreenUIState> = MutableStateFlow(HomeScreenUIState.Loading)
    val state: StateFlow<HomeScreenUIState> = _state

    val pagingData = Pager(
        config = PagingConfig(
            pageSize = 20
        )
    ) {
        pagingDataSource
    }.flow.cachedIn(viewModelScope)

    fun getHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = HomeScreenUIState.Loading
            runCatching {
                newsRepo.getNews()
            }.fold(
                onSuccess = {
                    val news = it.body()
                    if(news ==  null) {
                        _state.value = HomeScreenUIState.Error("Data not available")
                        return@fold
                    }
                    _state.value = HomeScreenUIState.Success(news.articles ?: emptyList())
                    articleDao.insertArticle(
                        ArticleEntity(
                            id = "${System.currentTimeMillis()}",
                            title = news.articles?.firstOrNull()?.title ?: "No title",
                            description = news.articles?.firstOrNull()?.description ?: "No description",
                            urlToImage = news.articles?.firstOrNull()?.urlToImage ?: "",
                            content = news.articles?.firstOrNull()?.content ?: "",
                            publishedAt = news.articles?.firstOrNull()?.publishedAt ?: "",
                            sourceId = news.articles?.firstOrNull()?.source?.id ?: "",
                            sourceName = news.articles?.firstOrNull()?.source?.name ?: "",
                            url = news.articles?.firstOrNull()?.url ?: "",
                            author = news.articles?.firstOrNull()?.author ?: ""
                        )
                    )
                    readDbData()
                    appDataStore.saveUserNameKey( "John Doe")
                    appDataStore.getUserNameKey()?.let {
                        Log.d("HomeViewModel", "User name from DataStore: $it")
                    }
                },
                onFailure = {
                    _state.value = HomeScreenUIState.Error(it.message ?: "Unknown error")
                }
            )
        }
    }


    fun readDbData() {
        viewModelScope.launch(Dispatchers.IO) {
            val articles = articleDao.getAllArticles()
            if(articles.isNotEmpty()) {
                Log.d("HomeViewModel", "Articles from DB: ${articles}")
            } else {
                Log.d("HomeViewModel", "No articles in DB")
            }
        }
    }

    fun updateState(){
        viewModelScope.launch {
            _state.debounce(500).collect {  }
        }
    }

}