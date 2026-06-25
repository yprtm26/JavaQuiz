package com.example.javaquiz.data.remote

import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.User

class AuthRepository {
    private val account get() = AppwriteClient.get().account

    suspend fun createAccount(name: String, email: String, password: String): User<Map<String, Any>> {
        return try {
            account.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = name
            )
        } catch (e: AppwriteException) {
            throw e
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            try {
                account.deleteSession("current")
            } catch (_: AppwriteException) {
            }
            account.createEmailPasswordSession(email, password)
        } catch (e: AppwriteException) {
            throw e
        }
    }

    suspend fun logout() {
        try {
            account.deleteSession("current")
        } catch (e: AppwriteException) {
            throw e
        }
    }

    suspend fun getCurrentUser(): User<Map<String, Any>>? {
        return try {
            account.get()
        } catch (e: AppwriteException) {
            null
        }
    }

    suspend fun updateName(name: String) {
        try {
            account.updateName(name)
        } catch (e: AppwriteException) {
            throw e
        }
    }

    suspend fun updateEmail(newEmail: String, password: String) {
        try {
            account.updateEmail(newEmail, password)
        } catch (e: AppwriteException) {
            throw e
        }
    }

    suspend fun updatePassword(newPassword: String, oldPassword: String) {
        try {
            account.updatePassword(newPassword, oldPassword)
        } catch (e: AppwriteException) {
            throw e
        }
    }
}
