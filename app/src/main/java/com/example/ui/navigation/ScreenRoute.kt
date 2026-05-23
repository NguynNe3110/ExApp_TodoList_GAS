package com.example.ui.navigation

sealed class ScreenRoute(val route: String) {
    data object Home : ScreenRoute("home")
}
