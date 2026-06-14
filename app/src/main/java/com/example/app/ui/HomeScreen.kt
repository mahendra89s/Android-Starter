package com.example.app.ui

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.app.model.internal.uistates.HomeScreenUIState
import com.example.app.navigation.HomeDetails
import com.example.app.viewmodel.HomeViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel
) {

    val state by homeViewModel.state.collectAsStateWithLifecycle()

    val pagingState = homeViewModel.pagingData.collectAsLazyPagingItems()

    val searchQuery by homeViewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.getHomeData()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            Button(
                onClick = {
                    navController.navigate(HomeDetails("abc"))
                }
            ) {
                Text(text = "Go to Details")
            }

            Text("-------------------Paging Data-------------------")
            LazyColumn() {
                stickyHeader {
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            homeViewModel.onSearchQueryChange(it)
                        }
                    )
                }
                items(pagingState.itemCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pagingState[it]?.title ?: "No title",
                            modifier = Modifier.weight(0.6f).padding(16.dp)
                        )
                        AsyncImage(
                            modifier = Modifier.weight(0.4f),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcQ_Zuz3haRHrSz0f3bnMlUTGa14Qc7Z5LLQ3-l04P98hv9CMXQU")
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            onState = { state ->
                                if (state is AsyncImagePainter.State.Error) {
                                    // Log this to Logcat to see the precise Exception thrown by Coil
                                    Log.e("CoilError", "Failed to load image", state.result.throwable)
                                }
                            }
                        )
                    }
                }

                if(pagingState.loadState.append is LoadState.Loading) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        }
                    }
                }

            }
        }
    }
}
