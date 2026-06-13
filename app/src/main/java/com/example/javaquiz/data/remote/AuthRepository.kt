package com.example.javaquiz.data.remote

import io.appwrite.ID
import io.appwrite.exceptions.AppwriteException
import io.appwrite.models.User

class AuthRepository {
    private val account = AppwriteClient.account

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
            // Hapus session yang masih aktif sebelum bikin baru
            try {
                account.deleteSession("current")
            } catch (_: AppwriteException) {
                // Tidak ada session aktif — lanjut
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
}
