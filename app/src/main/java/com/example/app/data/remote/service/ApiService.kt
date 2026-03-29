package com.example.app.data.remote.service

import com.example.app.model.network.NewsRM
import com.example.app.model.network.TopHeadlinesRM
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getNews(): Response<TopHeadlinesRM>

    @GET("everything")
    suspend fun getPaginatedNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<NewsRM>
}