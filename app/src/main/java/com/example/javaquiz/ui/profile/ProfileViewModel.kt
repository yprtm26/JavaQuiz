package com.example.javaquiz.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.remote.AppwriteClient
import com.example.javaquiz.data.remote.AuthRepository
import com.example.javaquiz.data.remote.QuizRepository
import com.example.javaquiz.data.remote.SessionData
import io.appwrite.ID
import io.appwrite.models.InputFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var userName by mutableStateOf("")
        private set

    var userEmail by mutableStateOf("")
        private set

    var bestScore by mutableStateOf(0)
        private set

    var completedQuizzes by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var profilePhotoPath by mutableStateOf<String?>(null)
        private set

    var photoUploading by mutableStateOf(false)
        private set

    var photoFileId by mutableStateOf<String?>(null)
        private set

    fun updatePhotoPath(path: String?) {
        profilePhotoPath = path
    }

    fun uploadPhoto(bytes: ByteArray) {
        viewModelScope.launch {
            photoUploading = true
            try {
                val storage = AppwriteClient.get().storage
                val file = withContext(Dispatchers.IO) {
                    storage.createFile(
                        bucketId = AppwriteClient.BUCKET_ID,
                        fileId = ID.unique(),
                        file = InputFile.fromBytes(bytes, "profile.jpg", "image/jpeg")
                    )
                }
                photoFileId = file.id
                profilePhotoPath = file.id
                SessionData.photoFileId = file.id
            } catch (_: Exception) {
                profilePhotoPath = null
            }
            photoUploading = false
        }
    }

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            isLoading = true
            val user = authRepository.getCurrentUser()
            if (user != null) {
                userName = user.name
                userEmail = user.email ?: ""
                val (best, completed) = QuizRepository.getUserStats(user.id)
                bestScore = best
                completedQuizzes = completed
            }
            photoFileId = SessionData.photoFileId.ifEmpty { null }
            isLoading = false
        }
    }

    fun updateProfile(
        newName: String,
        newEmail: String?,
        newPassword: String?,
        currentPassword: String,
        onResult: (success: Boolean, emailError: Boolean, passwordError: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            var emailFailed = false
            var passwordFailed = false
            try {
                if (newName != userName) {
                    authRepository.updateName(newName)
                    userName = newName
                    val user = authRepository.getCurrentUser()
                    if (user != null) {
                        QuizRepository.updateUserNameInHistory(user.id, newName)
                    }
                }
                if (newEmail != null && newEmail != userEmail) {
                    try {
                        authRepository.updateEmail(newEmail, currentPassword)
                        userEmail = newEmail
                    } catch (_: Exception) {
                        emailFailed = true
                    }
                }
                if (newPassword != null) {
                    try {
                        authRepository.updatePassword(newPassword, currentPassword)
                    } catch (_: Exception) {
                        passwordFailed = true
                    }
                }
                onResult(!emailFailed && !passwordFailed, emailFailed, passwordFailed)
            } catch (_: Exception) {
                onResult(false, emailFailed, passwordFailed)
            }
        }
    }

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logout()
                SessionData.photoFileId = ""
                onComplete()
            } catch (_: Exception) {
                onComplete()
            }
        }
    }
}
