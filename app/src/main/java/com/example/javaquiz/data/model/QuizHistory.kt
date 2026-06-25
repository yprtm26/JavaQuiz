package com.example.javaquiz.data.model

data class QuizHistory(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Int = 0,
    val userAnswers: Map<String, Int> = emptyMap(),
    val photoFileId: String = "",
    val date: Long = System.currentTimeMillis()
)
