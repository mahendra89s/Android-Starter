package com.example.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.app.ui.HomeDetails
import com.example.app.ui.HomeScreen
import com.example.app.viewmodel.HomeViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> {
            val homeViewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                navController = navController,
                homeViewModel = homeViewModel
            )
        }
        composable<HomeDetails> { backStackEntry ->
            val backStackEntry = remember(backStackEntry) {
                navController.getBackStackEntry<HomeRoute>()
            }
            val viewModel = hiltViewModel<HomeViewModel>(backStackEntry)
            HomeDetails(
                navController = navController,
                homeViewModel = viewModel
            )
        }
    }
}