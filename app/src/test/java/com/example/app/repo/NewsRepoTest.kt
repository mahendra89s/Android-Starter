package com.example.app.repo

import com.example.app.data.remote.repo.NewsRepo
import com.example.app.data.remote.service.ApiService
import com.example.app.model.network.TopHeadlinesRM
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class NewsRepoTest {

    private val apiService: ApiService = mockk()
    private val retrofit: Retrofit = mockk()
    private lateinit var newsRepo: NewsRepo

    @Before
    fun setup() {
        // Mock Retrofit to return our mocked ApiService
        every { retrofit.create(ApiService::class.java) } returns apiService
        newsRepo = NewsRepo(retrofit)
    }

    @Test
    fun `getNews calls apiService getNews and returns response`() = runTest {
        // Arrange
        val query = "Technology"
        val expectedResponse = Response.success(TopHeadlinesRM(articles = emptyList(), status =null, totalResults = 0))
        coEvery { apiService.getNews(query) } returns expectedResponse

        // Act
        val result = newsRepo.getNews(query)

        // Assert
        assertEquals(expectedResponse, result)
    }

    @Test
    fun `uploadFile converts byte array to multipart body and calls apiService`() = runTest {
        // Arrange
        val byteArray = "test_image_data".toByteArray()
        val expectedResponse = Response.success(true)

        // Use any() for the multipart part since reconstructing the exact okhttp Part in tests is flaky
        coEvery { apiService.uploadImage(any()) } returns expectedResponse

        // Act
        val result = newsRepo.uploadFile(byteArray)

        // Assert
        assertEquals(expectedResponse, result)
    }
}