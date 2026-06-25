package com.example.javaquiz.data.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

class AppwriteClient(context: Context) {
    val client = Client(context)
        .setEndpoint(API_ENDPOINT)
        .setProject(PROJECT_ID)
    val account = Account(client)
    val databases = Databases(client)
    val storage = Storage(client)
    val realtime = io.appwrite.services.Realtime(client)

    companion object {
        const val API_ENDPOINT = "https://nyc.cloud.appwrite.io/v1"
        const val PROJECT_ID = "javaquiz"
        const val DATABASE_ID = "javaquiz"
        const val BUCKET_ID = "assets"
        const val CATEGORIES_COLLECTION_ID = "categories"
        const val QUESTIONS_COLLECTION_ID = "questions"
        const val QUIZ_HISTORY_COLLECTION_ID = "quiz_histories"

        @Volatile
        private var instance: AppwriteClient? = null

        fun init(context: Context) {
            if (instance != null) return
            synchronized(this) {
                if (instance != null) return
                instance = AppwriteClient(context.applicationContext)
            }
        }

        fun get(): AppwriteClient = instance ?: error("AppwriteClient not initialized")
    }
}