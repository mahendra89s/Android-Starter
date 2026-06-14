package com.example.app.data.remote.repo

import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.TopHeadlinesRM
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class NewsRepo @Inject constructor(
    retrofit: Retrofit
): INewsRepo {
    private val apiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    override suspend fun getNews(searchQuery: String): Response<TopHeadlinesRM> {
        return apiService.getNews(searchQuery)
    }
}