package com.example.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.app.data.datastore.AppDataStore
import com.example.app.data.db.dao.ArticleDao
import com.example.app.data.db.entities.ArticleEntity
import com.example.app.data.remote.repo.INewsRepo
import com.example.app.model.internal.uistates.HomeScreenUIState
import com.example.app.model.network.ArticleRM
import com.example.app.pagingsource.PagingDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepo: INewsRepo,
    private val articleDao: ArticleDao,
    private val appDataStore: AppDataStore,
    private val retrofit: Retrofit
): ViewModel() {
    private val _state: MutableStateFlow<HomeScreenUIState> = MutableStateFlow(HomeScreenUIState())
    val state: StateFlow<HomeScreenUIState> = _state

    val searchQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val pagingData: Flow<PagingData<ArticleRM>> =
        searchQuery
            .debounce(300.milliseconds)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                Pager(
                    PagingConfig(pageSize = 20)
                ) {
                    PagingDataSource(retrofit, q)
                }.flow
            }.cachedIn(viewModelScope)

//    val pagingData = Pager(
//        config = PagingConfig(
//            pageSize = 20
//        )
//    ) {
//        pagingDataSource
//    }.flow.cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            _state
                .debounce(500)
                .distinctUntilChanged()
                .collect {
                    getHomeData()
            }
        }
    }
    fun getHomeData() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            runCatching {
                newsRepo.getNews()
            }.fold(
                onSuccess = { it ->
                    val news = it.body()
                    if(news ==  null) {
                        _state.update {
                            it.copy(
                                errorMessage = "Data not available",
                                isLoading = false
                            )
                        }
                        return@fold
                    }
                    _state.update {
                        it.copy(
                            articles = news.articles ?: emptyList(),
                            isLoading = false
                        )
                    }
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
                    _state.update {
                        it.copy(
                            errorMessage = it.errorMessage,
                            isLoading = false
                        )
                    }
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

    fun onSearchQueryChange(
        searchQuery: String
    ){
        this.searchQuery.value = searchQuery
    }

}