package com.example.app.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
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
                    navController.navigate(HomeDetails)
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
                    Text(
                        text = pagingState[it]?.title ?: "No title",
                        modifier = Modifier.padding(16.dp)
                    )
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

            Text("-------------------Normal Data-------------------")

            /*HomeContent(
                modifier = Modifier,
                state = state,
                onRetry = {
                    homeViewModel.getHomeData()
                }
            )*/
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier,
    state: HomeScreenUIState,
    onRetry: () -> Unit,
    onQuery: (String) -> Unit = {}
) {
    var textfield by remember {
        mutableStateOf(
            TextFieldValue("")
        )
    }
    var job: Job? = null
    val scope = rememberCoroutineScope()
    BasicTextField(
        value = textfield,
        onValueChange = {
            textfield = it
            job?.cancel()
            job = scope.launch {
                delay(500)
                onQuery(it.text)
            }
        }
    )

    if(state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if(state.errorMessage.isNotBlank()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = state.errorMessage)
            Button(
                onClick = {
                    onRetry()
                }
            ) {
                Text(text = "Retry")
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.articles.size) { index ->
            Text(
                text = state.articles[index].title ?: "No title",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}