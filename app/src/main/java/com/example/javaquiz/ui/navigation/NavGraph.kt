package com.example.javaquiz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.javaquiz.ui.auth.AuthViewModel
import com.example.javaquiz.ui.auth.LoginScreen
import com.example.javaquiz.ui.auth.RegisterScreen
import com.example.javaquiz.ui.history.HistoryScreen
import com.example.javaquiz.ui.home.HomeScreen
import com.example.javaquiz.ui.leaderboard.LeaderboardScreen
import com.example.javaquiz.ui.profile.ProfileScreen
import com.example.javaquiz.ui.quiz.DiscussionScreen
import com.example.javaquiz.ui.quiz.QuizScreen
import com.example.javaquiz.ui.quiz.QuizViewModel
import com.example.javaquiz.ui.quiz.ResultScreen
import com.example.javaquiz.data.remote.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val quizViewModel: QuizViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterNavigate = { navController.navigate(Screen.Register.route) },
                viewModel = authViewModel
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onLoginNavigate = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: return@composable
            QuizScreen(
                categoryName = categoryName,
                onQuizFinish = { score, total, timeUsed ->
                    navController.navigate(Screen.Result.createRoute(score, total, timeUsed)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel = quizViewModel
            )
        }
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("timeUsed") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0
            val timeUsed = backStackEntry.arguments?.getInt("timeUsed") ?: 0
            ResultScreen(
                score = score,
                total = total,
                timeUsed = timeUsed,
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onReview = {
                    navController.navigate(Screen.Review.route) {
                        popUpTo(Screen.Result.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController = navController)
        }
        composable(Screen.Review.route) {
            DiscussionScreen(
                onBackToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                viewModel = quizViewModel
            )
        }
        composable(Screen.History.route) {
            val scope = rememberCoroutineScope()
            HistoryScreen(
                navController = navController,
                onReviewArchived = { historyItem ->
                    scope.launch {
                        val questions = withContext(Dispatchers.IO) {
                            QuizRepository.getQuestionsByCategory(historyItem.categoryId)
                                .filter { it.id in historyItem.userAnswers }
                        }
                        quizViewModel.loadArchivedQuiz(historyItem.categoryId, questions, historyItem.userAnswers)
                        navController.navigate(Screen.Review.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                navController = navController,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
