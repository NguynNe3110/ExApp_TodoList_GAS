package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feature.auth.AuthSyncScreen
import com.example.feature.auth.AuthViewModel
import com.example.feature.home.HomeScreen
import com.example.feature.home.HomeViewModel
import com.example.ui.navigation.ScreenRoute
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize modern ViewModels using their custom Companion Factories linked to MyApp AppContainer
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)

                val navController = rememberNavController()
                val authState by authViewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ScreenRoute.Home.route,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        composable(route = ScreenRoute.Home.route) {
                            HomeScreen(
                                viewModel = homeViewModel,
                                registeredUser = authState.loggedInUser,
                                onNavigateToSync = {
                                    authViewModel.resetState()
                                    navController.navigate(ScreenRoute.AuthSync.route)
                                }
                            )
                        }

                        composable(route = ScreenRoute.AuthSync.route) {
                            AuthSyncScreen(
                                viewModel = authViewModel,
                                onNavigateBack = { loggedInUser ->
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
