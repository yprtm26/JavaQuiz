package com.example.javaquiz.data.model

data class Question(
    val id: String,
    val categoryId: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val explanation: String
)
