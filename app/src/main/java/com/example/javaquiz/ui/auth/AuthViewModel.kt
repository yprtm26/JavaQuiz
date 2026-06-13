package com.example.javaquiz.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.remote.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _registrationSuccess = mutableStateOf(false)
    val registrationSuccess: State<Boolean> = _registrationSuccess

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.createAccount(name, email, password)
                _registrationSuccess.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Registrasi gagal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.login(email, password)
                _registrationSuccess.value = true // Reusing this for navigation
            } catch (e: Exception) {
                _error.value = e.message ?: "Login gagal"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun resetRegistrationSuccess() {
        _registrationSuccess.value = false
    }
}
