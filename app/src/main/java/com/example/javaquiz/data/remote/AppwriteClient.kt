package com.example.javaquiz.data.remote

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage

object AppwriteClient {
    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases
    lateinit var storage: Storage // Tambahkan service Storage

    // Konfigurasi ID Utama Appwrite Anda
    const val DATABASE_ID = "javaquiz"
    const val BUCKET_ID = "assets" // ID Bucket tunggal milik Anda

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint("https://nyc.cloud.appwrite.io/v1") // Sesuai url di dashboard Anda
            .setProject("dbyarsi") // ID Project Appwrite Anda

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
    }
}