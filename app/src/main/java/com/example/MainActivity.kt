package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.feature.auth.AuthSyncScreen
import com.example.feature.auth.AuthViewModel
import com.example.feature.home.HomeScreen
import com.example.feature.home.HomeViewModel
import com.example.ui.navigation.ScreenRoute
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
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

                // Try to request highest available display mode for smoother refresh rate where supported
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        val display = windowManager.defaultDisplay
                        val modes = display.supportedModes
                        val best = modes.maxByOrNull { it.refreshRate }
                        best?.let { mode ->
                            val attrs = window.attributes
                            attrs.preferredDisplayModeId = mode.modeId
                            window.attributes = attrs
                        }
                    }
                } catch (_: Throwable) {}

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
