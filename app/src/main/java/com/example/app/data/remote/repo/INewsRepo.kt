package com.example.app.data.remote.repo

import com.example.app.model.network.TopHeadlinesRM
import retrofit2.Response

interface INewsRepo {
    suspend fun getNews(searchQuery: String = ""): Response<TopHeadlinesRM>

}