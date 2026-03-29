package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.app.data.datastore.DataStore
import com.example.app.data.db.dao.ArticleDao
import com.example.app.data.db.entities.ArticleEntity
import com.example.app.data.remote.repo.INewsRepo
import com.example.app.model.internal.uistates.HomeScreenUIState
import com.example.app.pagingsource.PagingDataSource
import com.example.app.utils.Resource
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
    private val dataStore: DataStore
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
            when(val news = newsRepo.getNews()){
                is Resource.Success -> {
                    _state.value = HomeScreenUIState.Success(news.data?.articles ?: emptyList())
                    articleDao.insertArticle(
                        ArticleEntity(
                            id = "${System.currentTimeMillis()}",
                            title = news.data?.articles?.firstOrNull()?.title ?: "No title",
                            description = news.data?.articles?.firstOrNull()?.description ?: "No description",
                            urlToImage = news.data?.articles?.firstOrNull()?.urlToImage ?: "",
                            content = news.data?.articles?.firstOrNull()?.content ?: "",
                            publishedAt = news.data?.articles?.firstOrNull()?.publishedAt ?: "",
                            sourceId = news.data?.articles?.firstOrNull()?.source?.id ?: "",
                            sourceName = news.data?.articles?.firstOrNull()?.source?.name ?: "",
                            url = news.data?.articles?.firstOrNull()?.url ?: "",
                            author = news.data?.articles?.firstOrNull()?.author ?: ""
                        )
                    )
                    readDbData()
                    dataStore.save(DataStore.USER_NAME_KEY.name, "John Doe")
                    dataStore.get(DataStore.USER_NAME_KEY.name)?.let {
                        Log.d("HomeViewModel", "User name from DataStore: $it")
                    }
                }
                is Resource.Error -> {
                    _state.value = HomeScreenUIState.Error(news.error ?: "Unknown error")
                }
            }
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