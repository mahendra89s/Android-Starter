package com.example.app.vm

import app.cash.turbine.test
import com.example.app.data.datastore.AppDataStore
import com.example.app.data.db.dao.ArticleDao
import com.example.app.data.remote.repo.INewsRepo
import com.example.app.model.network.TopHeadlinesRM
import com.example.app.utils.ILogger
import com.example.app.utils.MainDispatcherRule
import com.example.app.viewmodel.HomeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val newsRepo: INewsRepo = mockk()
    private val articleDao: ArticleDao = mockk(relaxed = true)
    private val appDataStore: AppDataStore = mockk(relaxed = true)
    private val retrofit: Retrofit = mockk(relaxed = true)
    private val logger: ILogger = mockk(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        viewModel = HomeViewModel(newsRepo, articleDao, appDataStore, retrofit, logger)
    }

    @Test
    fun `onSearchQueryChange updates searchQuery state flow`() = runTest {


        viewModel.onSearchQueryChange("Android News")

        assertEquals("Android News", viewModel.searchQuery.value)
    }

    @Test
    fun `getHomeData fetches news successfully and updates state`() = runTest {
        // Arrange
        val mockResponse = TopHeadlinesRM(articles = emptyList(), status = null, totalResults = 0) // Adjust based on your actual model
        coEvery { newsRepo.getNews() } returns Response.success(mockResponse)
        coEvery { appDataStore.getUserNameKey() } returns "John Doe"


        // Act
        viewModel.getHomeData()

        // Assert
        viewModel.state.test {
            val finalState = awaitItem()
            assertFalse(finalState.isLoading)
            assertEquals(emptyList<Any>(), finalState.articles)

            // Verify interactions
            coVerify(exactly = 1) { articleDao.insertArticle(any()) }
            coVerify(exactly = 1) { appDataStore.saveUserNameKey("John Doe") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `getHomeData emits error state on API failure`() = runTest {
        // Arrange
        coEvery { newsRepo.getNews() } throws RuntimeException("Network Error")

        // Act
        viewModel.getHomeData()

        // Assert
        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            // Note: In your catch block, you are setting errorMessage = it.errorMessage (which keeps the previous state).
            // If you intend to pass the actual exception message, you should update the VM to: errorMessage = it.message.
            cancelAndIgnoreRemainingEvents()
        }
    }
}