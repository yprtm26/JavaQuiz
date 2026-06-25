package com.example.javaquiz.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.model.Category
import com.example.javaquiz.data.remote.AuthRepository
import com.example.javaquiz.data.remote.QuizRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private var currentUserId: String = ""

    var bestScore by mutableStateOf(0)
        private set

    var completedQuizzes by mutableStateOf(0)
        private set

    var categories by mutableStateOf<List<Category>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var userName by mutableStateOf("Developer")
        private set

    init {
        fetchUserName()
        fetchCategories()
    }

    private fun fetchUserName() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                userName = user.name
                currentUserId = user.id
                fetchUserStats()
            }
        }
    }

    fun refreshStats() {
        if (currentUserId.isNotEmpty()) {
            fetchUserStats()
        } else {
            fetchUserName()
        }
    }

    private fun fetchUserStats() {
        viewModelScope.launch {
            val (best, completed) = QuizRepository.getUserStats(currentUserId)
            bestScore = best
            completedQuizzes = completed
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            isLoading = true
            val result = QuizRepository.getCategories()
            if (result.isNotEmpty()) {
                categories = result
            }
            isLoading = false
        }
    }
}
