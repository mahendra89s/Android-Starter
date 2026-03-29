package com.example.app.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.ArticleRM
import retrofit2.Retrofit

class PagingDataSource(
    retrofit: Retrofit
): PagingSource<Int, ArticleRM>() {
    private val apiService by lazy{
        retrofit.create(ApiService::class.java)
    }
    override fun getRefreshKey(state: PagingState<Int, ArticleRM>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleRM> {
        return try {
            val currentPage = params.key ?: 1
            val response = apiService.getNews()

            if (response.isSuccessful) {
                val news = response.body()?.articles ?: emptyList()
                LoadResult.Page(
                    data = news,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (news.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}