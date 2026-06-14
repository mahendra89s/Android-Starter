package com.example.app.data.remote.repo

import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.TopHeadlinesRM
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Multipart
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

    override suspend fun uploadFile(byteArray: ByteArray): Response<Boolean> {
        val requestBody = byteArray.toRequestBody(
            contentType = "image/jpeg".toMediaType(),
            byteCount = byteArray.size
        )

        val multipartRequest = MultipartBody.Part.createFormData(
            name = "image",// Backend gives
            filename = "pic.jpeg",
            body = requestBody
        )

        return apiService.uploadImage(multipartRequest)
    }
}