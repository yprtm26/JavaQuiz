package com.example.javaquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import com.example.javaquiz.ui.auth.LoginScreen
import com.example.javaquiz.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { navController.navigate(Screen.Home.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            Text("Register Screen")
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Quiz.route) {
            Text("Quiz Screen")
        }
        composable(Screen.Result.route) {
            Text("Result Screen")
        }
        composable(Screen.Review.route) {
            Text("Review Screen")
        }
        composable(Screen.Leaderboard.route) {
            Text("Leaderboard Screen")
        }
        composable(Screen.History.route) {
            Text("History Screen")
        }
        composable(Screen.Profile.route) {
            Text("Profile Screen")
        }
    }
}
