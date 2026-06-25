package com.example.javaquiz.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscussionScreen(
    onBackToHome: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    var currentReviewIndex by remember { mutableIntStateOf(0) }
    var prevPressed by remember { mutableStateOf(false) }
    var nextPressed by remember { mutableStateOf(false) }
    val prevScale by animateFloatAsState(
        targetValue = if (prevPressed) 0.97f else 1f,
        animationSpec = tween(100)
    )
    val nextScale by animateFloatAsState(
        targetValue = if (nextPressed) 0.97f else 1f,
        animationSpec = tween(100)
    )
    val prevInteractionSource = remember { MutableInteractionSource() }
    val nextInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(prevInteractionSource) {
        prevInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> prevPressed = true
                is PressInteraction.Release -> prevPressed = false
                is PressInteraction.Cancel -> prevPressed = false
            }
        }
    }

    LaunchedEffect(nextInteractionSource) {
        nextInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> nextPressed = true
                is PressInteraction.Release -> nextPressed = false
                is PressInteraction.Cancel -> nextPressed = false
            }
        }
    }

    val questions = viewModel.questions
    val total = questions.size
    val question = questions.getOrNull(currentReviewIndex) ?: return
    val correctAnswer = viewModel.getCorrectAnswer(question.id)
    val userAnswer = viewModel.getUserAnswer(question.id)
    val isCorrect = userAnswer == correctAnswer

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pembahasan",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHome) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = viewModel.categoryDisplayName.ifEmpty { "Java" },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (currentReviewIndex > 0) currentReviewIndex-- },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .scale(prevScale),
                        shape = RoundedCornerShape(12.dp),
                        enabled = currentReviewIndex > 0,
                        interactionSource = prevInteractionSource
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sebelumnya",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = {
                            if (currentReviewIndex < total - 1) {
                                currentReviewIndex++
                            } else {
                                onBackToHome()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .scale(nextScale),
                        shape = RoundedCornerShape(12.dp),
                        interactionSource = nextInteractionSource,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (currentReviewIndex < total - 1) "Selanjutnya" else "Selesai",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STATUS KUIS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Pertanyaan ${currentReviewIndex + 1} dari $total",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = iconFromName(viewModel.categoryIconName),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = viewModel.categoryDisplayName.ifEmpty { "Java" },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val qText = question.question
                    val hasCode = qText.contains("\n")

                    Text(
                        text = if (hasCode) qText.substringBefore("\n") else qText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 24.sp
                    )

                    if (hasCode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = qText.substringAfter("\n"),
                                fontSize = 13.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = Color(0xFFF8FAFC),
                                lineHeight = 20.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        question.options.forEachIndexed { idx, opt ->
                            val isCorrectAns = correctAnswer == idx
                            val isUserWrong = userAnswer == idx && !isCorrectAns

                            val optionBg = when {
                                isCorrectAns -> MaterialTheme.colorScheme.primaryContainer
                                isUserWrong -> MaterialTheme.colorScheme.errorContainer
                                else -> MaterialTheme.colorScheme.surfaceContainerLow
                            }
                            val optionBorder = when {
                                isCorrectAns -> MaterialTheme.colorScheme.primary
                                isUserWrong -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.outlineVariant
                            }
                            val optionAlpha = if (!isCorrectAns && !isUserWrong) 0.6f else 1f

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(optionBg.copy(alpha = optionAlpha))
                                    .border(
                                        width = if (isCorrectAns) 2.dp else 1.dp,
                                        color = optionBorder.copy(alpha = optionAlpha),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .then(
                                            if (isCorrectAns) Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.primary)
                                            else if (isUserWrong) Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.error)
                                            else Modifier.border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCorrectAns) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
                                    } else if (isUserWrong) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onError, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(
                                    text = opt,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = when {
                                        isCorrectAns -> MaterialTheme.colorScheme.onPrimaryContainer
                                        isUserWrong -> MaterialTheme.colorScheme.error
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            val accentColor = MaterialTheme.colorScheme.primary
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRect(
                            accentColor,
                            Offset.Zero,
                            Size(4.dp.toPx(), size.height)
                        )
                    },
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "\uD83D\uDCA1", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Penjelasan",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Start
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun iconFromName(name: String): ImageVector = when (name) {
    "account_tree" -> Icons.Filled.AccountTree
    "refresh" -> Icons.Filled.Refresh
    "text_fields" -> Icons.Filled.TextFields
    "grid_view" -> Icons.Filled.GridView
    else -> Icons.Filled.Code
}
