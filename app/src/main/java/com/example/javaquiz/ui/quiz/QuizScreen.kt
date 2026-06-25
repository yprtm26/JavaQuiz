package com.example.javaquiz.ui.quiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    categoryName: String,
    onQuizFinish: (Int, Int, Int) -> Unit,
    onBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var nextPressed by remember { mutableStateOf(false) }
    val nextScale by animateFloatAsState(
        targetValue = if (nextPressed) 0.95f else 1f,
        animationSpec = tween(100)
    )
    val quizInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(quizInteractionSource) {
        quizInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> nextPressed = true
                is PressInteraction.Release -> nextPressed = false
                is PressInteraction.Cancel -> nextPressed = false
            }
        }
    }

    LaunchedEffect(categoryName) {
        viewModel.loadQuestions(categoryName)
    }

    LaunchedEffect(viewModel.isFinished) {
        if (viewModel.isFinished) {
            onQuizFinish(viewModel.score, viewModel.totalQuestions, viewModel.timeUsed)
        }
    }

    LaunchedEffect(viewModel.loadError) {
        viewModel.loadError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(viewModel.saveError) {
        viewModel.saveError?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    val question = viewModel.currentQuestion

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Terminal,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (categoryName) {
                                "looping" -> "Kuis Looping"
                                "inheritance" -> "Kuis Inheritance"
                                "string" -> "Kuis String"
                                "array" -> "Kuis Array"
                                else -> "Kuis"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.finishQuiz()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    val timerColor = if (viewModel.remainingSeconds <= 60)
                        Color(0xFFDC2626)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant

                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = null,
                                tint = timerColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = viewModel.formattedTime,
                                style = MaterialTheme.typography.labelMedium,
                                color = timerColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        if (question == null) return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pertanyaan ${viewModel.currentIndex + 1} dari ${viewModel.totalQuestions}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${viewModel.progress.toInt()}% Selesai",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { viewModel.progress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )

            Spacer(modifier = Modifier.height(24.dp))

            val questionText = question.question
            val hasCode = questionText.contains("\n")

            if (hasCode) {
                val parts = questionText.split("\n", limit = 2)
                Text(
                    text = parts[0],
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = parts.getOrElse(1) { "" },
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFF8FAFC),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Text(
                    text = questionText,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = viewModel.selectedAnswer == index

                val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                val cardBg = if (isSelected) MaterialTheme.colorScheme.primaryFixed.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceContainerLowest
                val indicatorBg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer
                val indicatorTextColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
                    onClick = { viewModel.selectAnswer(index) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = indicatorBg,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = ('A' + index).toString(),
                                    fontWeight = FontWeight.SemiBold,
                                    color = indicatorTextColor,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.nextQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .scale(nextScale),
                shape = RoundedCornerShape(50),
                enabled = viewModel.isAnswered,
                interactionSource = quizInteractionSource,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = if (viewModel.currentIndex == viewModel.questions.lastIndex) "Lihat Hasil" else "Selanjutnya",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
