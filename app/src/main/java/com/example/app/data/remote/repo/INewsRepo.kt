package com.example.app.data.remote.repo

import com.example.app.model.network.TopHeadlinesRM
import com.example.app.utils.Resource

interface INewsRepo {
    suspend fun getNews(): Resource<TopHeadlinesRM?, String>
}