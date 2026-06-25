package com.example.javaquiz.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.javaquiz.data.model.Question
import com.example.javaquiz.data.model.QuizQuestion
import com.example.javaquiz.data.remote.AuthRepository
import com.example.javaquiz.data.remote.QuizRepository
import com.example.javaquiz.data.remote.SessionData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {

    private var allQuestions = emptyList<Question>()
    private var userAnswers = mutableMapOf<String, Int>()
    private var timerJob: Job? = null
    private val authRepository = AuthRepository()

    private var currentCategoryId: String = ""
    private var userId: String = ""
    private var userName: String = ""
    private var photoFileId: String = ""
    private var totalTimeSeconds = 0

    var categoryDisplayName by mutableStateOf("")
        private set

    var categoryIconName by mutableStateOf("code")
        private set

    var questions by mutableStateOf<List<QuizQuestion>>(emptyList())
        private set

    var currentIndex by mutableIntStateOf(0)
        private set

    var isFinished by mutableStateOf(false)
        private set

    var selectedAnswer by mutableIntStateOf(-1)
        private set

    var isAnswered by mutableStateOf(false)
        private set

    var remainingSeconds by mutableIntStateOf(0)
        private set

    var loadError by mutableStateOf<String?>(null)
        private set

    var saveError by mutableStateOf<String?>(null)
        private set

    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentIndex)

    val totalQuestions: Int
        get() = questions.size

    val progress: Int
        get() = if (totalQuestions > 0) ((currentIndex + 1) * 100) / totalQuestions else 0

    val score: Int
        get() = allQuestions.count { q -> userAnswers[q.id] == q.correctAnswer }

    val timeUsed: Int
        get() = totalTimeSeconds - remainingSeconds

    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

    fun loadQuestions(categoryId: String, questionCount: Int = 10, totalMinutes: Int = 10) {
        currentCategoryId = categoryId
        val id = categoryId.lowercase()
        categoryDisplayName = when (id) {
            "looping" -> "Perulangan"
            "inheritance" -> "Inheritance"
            "string" -> "String"
            "array" -> "Array"
            else -> "Java"
        }
        categoryIconName = when (id) {
            "looping" -> "refresh"
            "inheritance" -> "account_tree"
            "string" -> "text_fields"
            "array" -> "grid_view"
            else -> "code"
        }
        isFinished = false
        userAnswers.clear()
        selectedAnswer = -1
        isAnswered = false
        totalTimeSeconds = totalMinutes * 60
        remainingSeconds = totalTimeSeconds
        currentIndex = 0
        loadError = null

        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            if (user == null) {
                loadError = "Sesi telah berakhir. Silakan login kembali."
                return@launch
            }
            userId = user.id
            userName = user.name
            photoFileId = SessionData.photoFileId

            allQuestions = QuizRepository.getQuestionsByCategory(categoryId)
            questions = if (allQuestions.isNotEmpty()) {
                allQuestions.shuffled().take(questionCount).map { q ->
                    QuizQuestion(
                        id = q.id,
                        question = q.question,
                        options = q.options,
                        explanation = q.explanation
                    )
                }
            } else {
                emptyList()
            }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }
            finishQuiz()
        }
    }

    fun selectAnswer(answerIndex: Int) {
        selectedAnswer = answerIndex
        isAnswered = true
        userAnswers[currentQuestion?.id ?: ""] = answerIndex
    }

    private suspend fun saveProgress() {
        if (userId.isEmpty()) {
            saveError = "Gagal menyimpan: userId kosong"
            return
        }
        val total = totalQuestions
        val currentScore = score
        try {
            QuizRepository.saveQuizResult(
                userId = userId,
                userName = userName,
                categoryId = currentCategoryId,
                categoryName = categoryDisplayName.ifEmpty { currentCategoryId },
                score = currentScore,
                totalQuestions = total,
                percentage = if (total > 0) (currentScore * 100) / total else 0,
                userAnswersJson = org.json.JSONObject(userAnswers.toMap()).toString(),
                photoFileId = photoFileId
            )
        } catch (e: Exception) {
            saveError = "Gagal menyimpan hasil: ${e.message}"
        }
    }

    fun loadArchivedQuiz(categoryId: String, archivedQuestions: List<Question>, archivedUserAnswers: Map<String, Int>) {
        currentCategoryId = categoryId
        val id = categoryId.lowercase()
        categoryDisplayName = when (id) {
            "looping" -> "Perulangan"
            "inheritance" -> "Inheritance"
            "string" -> "String"
            "array" -> "Array"
            else -> "Java"
        }
        categoryIconName = when (id) {
            "looping" -> "refresh"
            "inheritance" -> "account_tree"
            "string" -> "text_fields"
            "array" -> "grid_view"
            else -> "code"
        }
        allQuestions = archivedQuestions
        questions = archivedQuestions.map { q ->
            QuizQuestion(
                id = q.id,
                question = q.question,
                options = q.options,
                explanation = q.explanation
            )
        }
        userAnswers.clear()
        userAnswers.putAll(archivedUserAnswers)
        isFinished = true
        currentIndex = 0
        selectedAnswer = -1
        isAnswered = archivedUserAnswers.isNotEmpty()
        timerJob?.cancel()
        remainingSeconds = 0
        loadError = null
        saveError = null
    }

    fun nextQuestion() {
        if (currentIndex < questions.lastIndex) {
            currentIndex++
            selectedAnswer = -1
            isAnswered = false
        } else {
            finishQuiz()
        }
    }

    fun finishQuiz() {
        timerJob?.cancel()
        viewModelScope.launch {
            saveError = null
            saveProgress()
            if (saveError == null) {
                isFinished = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun clearErrors() {
        loadError = null
        saveError = null
    }

    fun getCorrectAnswer(questionId: String): Int {
        return allQuestions.find { it.id == questionId }?.correctAnswer ?: -1
    }

    fun getUserAnswer(questionId: String): Int {
        return userAnswers[questionId] ?: -1
    }
}
