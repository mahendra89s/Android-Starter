package com.example.app.data.remote.repo

import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.TopHeadlinesRM
import com.example.app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import javax.inject.Inject

class NewsRepo @Inject constructor(
    retrofit: Retrofit
): INewsRepo {
    private val apiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    override suspend fun getNews(): Resource<TopHeadlinesRM?, String> {
        return withContext(Dispatchers.IO) {
            val res = apiService.getNews()
            if(res.isSuccessful) {
                Resource.Success(res.body())
            } else {
                Resource.Error(res.errorBody()?.string())
            }
        }
    }
}