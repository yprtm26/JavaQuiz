package com.example.javaquiz.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Quiz : Screen("quiz/{categoryName}") {
        fun createRoute(categoryName: String) = "quiz/$categoryName"
    }
    object Result : Screen("result/{score}/{total}/{timeUsed}") {
        fun createRoute(score: Int, total: Int, timeUsed: Int) = "result/$score/$total/$timeUsed"
    }
    object Review : Screen("review")
    object Leaderboard : Screen("leaderboard")
    object History : Screen("history")
    object Profile : Screen("profile")
}
