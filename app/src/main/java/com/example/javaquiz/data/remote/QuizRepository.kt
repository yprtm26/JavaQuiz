package com.example.javaquiz.data.remote

import com.example.javaquiz.data.model.Category
import com.example.javaquiz.data.model.Question
import com.example.javaquiz.data.model.QuizHistory
import io.appwrite.ID
import io.appwrite.models.Document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object QuizRepository {
    private val databases get() = AppwriteClient.get().databases
    private val DATABASE_ID = AppwriteClient.DATABASE_ID
    private val CATEGORIES_COLLECTION_ID = AppwriteClient.CATEGORIES_COLLECTION_ID
    private val QUESTIONS_COLLECTION_ID = AppwriteClient.QUESTIONS_COLLECTION_ID
    private val QUIZ_HISTORY_COLLECTION_ID = AppwriteClient.QUIZ_HISTORY_COLLECTION_ID

    suspend fun getCategories(): List<Category> = withContext(Dispatchers.IO) {
        try {
            val response = databases.listDocuments(DATABASE_ID, CATEGORIES_COLLECTION_ID)
            response.documents.map { doc ->
                Category(
                    id = doc.id,
                    name = doc.data["name"] as? String ?: "",
                    description = doc.data["description"] as? String ?: "",
                    iconName = doc.data["icon"] as? String ?: "code"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getQuestionsByCategory(categoryId: String): List<Question> = withContext(Dispatchers.IO) {
        try {
            val queries = listOf(
                io.appwrite.Query.equal("category_id", categoryId)
            )
            val response = databases.listDocuments(DATABASE_ID, QUESTIONS_COLLECTION_ID, queries)
            response.documents.map { doc -> documentToQuestion(doc) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getAllQuestions(): List<Question> = withContext(Dispatchers.IO) {
        try {
            val response = databases.listDocuments(DATABASE_ID, QUESTIONS_COLLECTION_ID)
            response.documents.map { doc -> documentToQuestion(doc) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveQuizResult(
        userId: String,
        userName: String,
        categoryId: String,
        categoryName: String,
        score: Int,
        totalQuestions: Int,
        percentage: Int,
        userAnswersJson: String = "{}",
        photoFileId: String = ""
    ) = withContext(Dispatchers.IO) {
        val data = mutableMapOf<String, Any>(
            "user_id" to userId,
            "user_name" to userName,
            "category_id" to categoryId,
            "category_name" to categoryName,
            "score" to score,
            "total_questions" to totalQuestions,
            "percentage" to percentage,
            "user_answers" to userAnswersJson
        )
        // photo_file_id disimpan jika ada, tidak wajib
        if (photoFileId.isNotEmpty()) {
            data["photo_file_id"] = photoFileId
        }
        databases.createDocument(DATABASE_ID, QUIZ_HISTORY_COLLECTION_ID, ID.unique(), data)
    }

    suspend fun getLeaderboard(): List<QuizHistory> = withContext(Dispatchers.IO) {
        try {
            val queries = listOf(
                io.appwrite.Query.limit(200)
            )
            val response = databases.listDocuments(DATABASE_ID, QUIZ_HISTORY_COLLECTION_ID, queries)
            val allResults = response.documents.map { doc -> documentToQuizHistory(doc) }
            allResults.groupBy { it.userId }
                .map { (_, entries) ->
                    val totalPoints = entries.sumOf { it.score * 10 }
                    val latest = entries.maxByOrNull { it.date } ?: entries.first()
                    latest.copy(percentage = totalPoints)
                }
                .sortedByDescending { it.percentage }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserStats(userId: String): Pair<Int, Int> = withContext(Dispatchers.IO) {
        try {
            val queries = listOf(
                io.appwrite.Query.equal("user_id", userId)
            )
            val response = databases.listDocuments(DATABASE_ID, QUIZ_HISTORY_COLLECTION_ID, queries)
            val results = response.documents.map { doc -> documentToQuizHistory(doc) }
            val totalPoints = results.sumOf { it.score * 10 }
            val completedQuizzes = results.size
            Pair(totalPoints, completedQuizzes)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(0, 0)
        }
    }

    suspend fun getQuizHistory(userId: String): List<QuizHistory> = withContext(Dispatchers.IO) {
        try {
            val queries = listOf(
                io.appwrite.Query.equal("user_id", userId),
                io.appwrite.Query.orderDesc("\$createdAt")
            )
            val response = databases.listDocuments(DATABASE_ID, QUIZ_HISTORY_COLLECTION_ID, queries)
            response.documents.map { doc -> documentToQuizHistory(doc) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun documentToQuizHistory(doc: Document<Map<String, Any>>): QuizHistory {
        return QuizHistory(
            id = doc.id,
            userId = doc.data["user_id"] as? String ?: "",
            userName = doc.data["user_name"] as? String ?: "",
            categoryId = doc.data["category_id"] as? String ?: "",
            categoryName = doc.data["category_name"] as? String ?: "",
            score = (doc.data["score"] as? Number)?.toInt() ?: 0,
            totalQuestions = (doc.data["total_questions"] as? Number)?.toInt() ?: 0,
            percentage = (doc.data["percentage"] as? Number)?.toInt() ?: 0,
            photoFileId = doc.data["photo_file_id"] as? String ?: "",
            userAnswers = try {
                val str = (doc.data["user_answers"] as? String) ?: "{}"
                val obj = org.json.JSONObject(str)
                obj.keys().asSequence().map { key -> key to obj.getInt(key) }.toMap()
            } catch (_: Exception) { emptyMap() },
            date = (doc.data["\$createdAt"] as? String)?.let {
                try { java.time.Instant.parse(it).toEpochMilli() } catch (_: Exception) { null }
            } ?: System.currentTimeMillis()
        )
    }

    private fun documentToQuestion(doc: Document<Map<String, Any>>): Question {
        val correctAnswerStr = (doc.data["correct_answer"] as? String)?.uppercase() ?: "A"
        val correctIndex = when (correctAnswerStr) {
            "A" -> 0
            "B" -> 1
            "C" -> 2
            "D" -> 3
            else -> 0
        }
        return Question(
            id = doc.id,
            categoryId = doc.data["category_id"] as? String ?: "",
            question = doc.data["question_text"] as? String ?: "",
            options = listOf(
                doc.data["option_a"] as? String ?: "",
                doc.data["option_b"] as? String ?: "",
                doc.data["option_c"] as? String ?: "",
                doc.data["option_d"] as? String ?: ""
            ),
            correctAnswer = correctIndex,
            explanation = doc.data["explanation"] as? String ?: ""
        )
    }
}
