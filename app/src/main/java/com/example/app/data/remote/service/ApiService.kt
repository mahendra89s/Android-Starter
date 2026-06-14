package com.example.app.data.remote.service

import com.example.app.model.network.NewsRM
import com.example.app.model.network.TopHeadlinesRM
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    suspend fun getNews(
        @Query("q") searchQuery: String
    ): Response<TopHeadlinesRM>

    @GET("everything")
    suspend fun getPaginatedNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<NewsRM>

    @Multipart
    @POST("v2/upload-image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<Boolean> // Replace with your actual response data model
}