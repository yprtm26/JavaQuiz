package com.example.javaquiz.data.remote

import android.content.Context

object SessionData {
    private const val PREFS_NAME = "javaquiz_prefs"
    private const val KEY_PHOTO_FILE_ID = "photoFileId"

    private var prefs: android.content.SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var photoFileId: String
        get() = prefs?.getString(KEY_PHOTO_FILE_ID, "") ?: ""
        set(value) {
            prefs?.edit()?.putString(KEY_PHOTO_FILE_ID, value)?.apply()
        }
}
