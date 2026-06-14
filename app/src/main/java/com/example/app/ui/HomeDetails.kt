package com.example.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.app.viewmodel.HomeViewModel

@Composable
fun HomeDetails(
    navController: NavController,
    homeViewModel: HomeViewModel,
    articleName: String
) {
    val state by homeViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            Text(text = articleName)
            Button(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(text = "Go Back")
            }
            HomeContent(
                modifier = Modifier.padding(paddingValues),
                state = state,
                onRetry = {
                    homeViewModel.getHomeData()
                }
            )
        }
    }
}