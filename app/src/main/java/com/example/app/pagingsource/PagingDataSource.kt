package com.example.app.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.ArticleRM
import retrofit2.Retrofit

class PagingDataSource(
    retrofit: Retrofit,
    val searchQuery: String
): PagingSource<Int, ArticleRM>() {
    private val apiService by lazy{
        retrofit.create(ApiService::class.java)
    }
    override fun getRefreshKey(state: PagingState<Int, ArticleRM>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleRM> {
        val currentPage = params.key ?: 1
        return runCatching {
                apiService.getNews(searchQuery = searchQuery)
            }.fold(
                onSuccess = {
                    val news = it.body()?.articles ?: emptyList()
                    LoadResult.Page(
                        data = news,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = if (news.isEmpty()) null else currentPage + 1
                    )
                },
                onFailure = {
                    LoadResult.Error(Exception("Error: ${it.message}"))
                }
            )
    }
}