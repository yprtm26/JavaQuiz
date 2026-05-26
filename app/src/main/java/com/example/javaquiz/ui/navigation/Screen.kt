package com.example.javaquiz.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Quiz : Screen("quiz")
    object Result : Screen("result")
    object Review : Screen("review")
    object Leaderboard : Screen("leaderboard")
    object History : Screen("history")
    object Profile : Screen("profile")
}
