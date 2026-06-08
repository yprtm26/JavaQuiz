package com.example.javaquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.javaquiz.ui.auth.AuthViewModel
import com.example.javaquiz.ui.auth.LoginScreen
import com.example.javaquiz.ui.auth.RegisterScreen
import com.example.javaquiz.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) },
                onGoogleLoginClick = { /* Handle Google Login */ },
                onRegisterNavigate = { navController.navigate(Screen.Register.route) },
                viewModel = authViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { /* Handle registration logic */ },
                onLoginNavigate = { navController.popBackStack() }
            )
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
