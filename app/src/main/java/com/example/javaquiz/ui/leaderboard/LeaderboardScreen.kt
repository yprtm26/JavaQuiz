package com.example.javaquiz.ui.leaderboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.javaquiz.data.model.QuizHistory
import com.example.javaquiz.ui.navigation.AppBottomNavigationBar
import com.example.javaquiz.ui.theme.Gold
import com.example.javaquiz.ui.theme.HighlightBg
import com.example.javaquiz.ui.theme.HighlightText
import com.example.javaquiz.ui.theme.Silver
import com.example.javaquiz.ui.theme.Bronze
import com.example.javaquiz.ui.theme.Success
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import com.example.javaquiz.data.remote.AppwriteClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navController: NavHostController,
    viewModel: LeaderboardViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            viewModel.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            viewModel.error != null && viewModel.leaderboard.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(Icons.Filled.EmojiEvents, null, tint = MaterialTheme.colorScheme.surfaceContainerHighest, modifier = Modifier.size(80.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = viewModel.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedButton(onClick = { viewModel.fetchLeaderboard() }) {
                            Text(
                                text = "Refresh",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(top = 24.dp).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item { Header() }
                    item { TableHeader() }
                    itemsIndexed(viewModel.leaderboard) { index, entry ->
                        LeaderboardRow(rank = index + 1, entry = entry, isCurrentUser = entry.userId == viewModel.currentUserId)
                    }
                }

                if (viewModel.currentUserRank > 0) {
                    val currentEntry = viewModel.leaderboard.find { it.userId == viewModel.currentUserId }
                    if (currentEntry != null) {
                        CurrentUserCard(rank = viewModel.currentUserRank, entry = currentEntry, totalParticipants = viewModel.totalParticipants)
                    }
                }
            }
        }

        AppBottomNavigationBar(navController = navController)
    }
}

@Composable
private fun Header() {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = "Leaderboard",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(Success))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Peringkat diupdate secara langsung",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TableHeader() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            text = "No.",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = "Pengguna",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Skor",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
private fun LeaderboardRow(rank: Int, entry: QuizHistory, isCurrentUser: Boolean) {
    val bgColor = if (isCurrentUser) HighlightBg else MaterialTheme.colorScheme.surfaceContainerLowest
    val borderColor = if (isCurrentUser) HighlightText.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outlineVariant

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            RankBadge(rank = rank, isCurrentUser = isCurrentUser)
            Spacer(Modifier.width(16.dp))
            UserAvatar(name = entry.userName, photoFileId = entry.photoFileId)
            Spacer(Modifier.width(12.dp))
            Text(
                text = entry.userName.ifEmpty { "User" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isCurrentUser) HighlightText else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${entry.percentage}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isCurrentUser) HighlightText else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp)
            )
        }
    }
}

@Composable
private fun RankBadge(rank: Int, isCurrentUser: Boolean) {
    val color = when (rank) {
        1 -> Gold; 2 -> Silver; 3 -> Bronze
        else -> Color.Transparent
    }
    if (rank <= 3) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Text("$rank", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    } else {
        Text(
            text = "$rank",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isCurrentUser) HighlightText else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun UserAvatar(name: String, photoFileId: String = "") {
    val photoBitmap = remember(photoFileId) { mutableStateOf<android.graphics.Bitmap?>(null) }
    LaunchedEffect(photoFileId) {
        if (photoFileId.isNotEmpty()) {
            photoBitmap.value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val url = "${AppwriteClient.API_ENDPOINT}/storage/buckets/${AppwriteClient.BUCKET_ID}/files/$photoFileId/view?project=${AppwriteClient.PROJECT_ID}"
                    val connection = java.net.URL(url).openConnection()
                    connection.setRequestProperty("Content-Type", "application/octet-stream")
                    val inputStream = connection.getInputStream()
                    inputStream.use { BitmapFactory.decodeStream(it) }
                } catch (_: Exception) { null }
            }
        } else {
            photoBitmap.value = null
        }
    }
    val initial = name.firstOrNull()?.uppercase() ?: "U"
    Box(
        Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (photoBitmap.value != null) {
            Image(
                bitmap = photoBitmap.value!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else if (name.isNotEmpty()) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Icon(Icons.Filled.Person, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun CurrentUserCard(rank: Int, entry: QuizHistory, totalParticipants: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = HighlightBg,
        border = BorderStroke(1.dp, HighlightText.copy(alpha = 0.2f)),
        tonalElevation = 4.dp
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(48.dp)) {
                    UserAvatar(name = entry.userName, photoFileId = entry.photoFileId)
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(HighlightText)
                            .border(2.dp, HighlightBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("$rank", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Peringkatmu",
                        style = MaterialTheme.typography.labelSmall,
                        color = HighlightText.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "#$rank dari $totalParticipants",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = HighlightText
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${entry.percentage}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = HighlightText
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.EmojiEvents, null, tint = Gold, modifier = Modifier.size(32.dp))
            }
        }
    }
}
