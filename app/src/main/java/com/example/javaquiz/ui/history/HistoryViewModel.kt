package com.example.javaquiz.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.model.QuizHistory
import com.example.javaquiz.data.remote.AuthRepository
import com.example.javaquiz.data.remote.QuizRepository
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var history by mutableStateOf<List<QuizHistory>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            isLoading = true
            val user = authRepository.getCurrentUser()
            if (user != null) {
                history = QuizRepository.getQuizHistory(user.id)
            }
            isLoading = false
        }
    }
}
