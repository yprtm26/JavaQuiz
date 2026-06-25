package com.example.javaquiz.ui.leaderboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.model.QuizHistory
import com.example.javaquiz.data.remote.AppwriteClient
import com.example.javaquiz.data.remote.AuthRepository
import com.example.javaquiz.data.remote.QuizRepository
import io.appwrite.models.RealtimeSubscription
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {

    var leaderboard by mutableStateOf<List<QuizHistory>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var currentUserId by mutableStateOf("")
        private set

    var currentUserRank by mutableIntStateOf(0)
        private set

    var totalParticipants by mutableIntStateOf(0)
        private set

    private var realtimeSubscription: RealtimeSubscription? = null

    init {
        viewModelScope.launch {
            val user = AuthRepository().getCurrentUser()
            currentUserId = user?.id ?: ""
            fetchLeaderboard()
            subscribeToRealtime()
        }
    }

    private fun subscribeToRealtime() {
        val channel = "databases.${AppwriteClient.DATABASE_ID}.collections.${AppwriteClient.QUIZ_HISTORY_COLLECTION_ID}.documents"
        try {
            realtimeSubscription = AppwriteClient.get().realtime.subscribe(
                channels = arrayOf(channel),
                callback = { _ ->
                    fetchLeaderboard()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fetchLeaderboard() {
        viewModelScope.launch {
            isLoading = true
            error = null
            val result = QuizRepository.getLeaderboard()
            if (result.isNotEmpty()) {
                leaderboard = result
                totalParticipants = result.size
                currentUserRank = if (currentUserId.isNotEmpty()) {
                    val index = result.indexOfFirst { it.userId == currentUserId }
                    if (index >= 0) index + 1 else 0
                } else 0
            } else {
                error = "Belum ada data leaderboard"
            }
            isLoading = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            realtimeSubscription?.close()
        } catch (_: Exception) {}
    }
}
