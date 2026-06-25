package com.example.javaquiz.data.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val questionCount: Int = 0
)
