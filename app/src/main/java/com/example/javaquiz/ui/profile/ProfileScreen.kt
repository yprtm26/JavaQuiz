package com.example.javaquiz.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.javaquiz.ui.navigation.AppBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    var isLoggingOut by remember { mutableStateOf(false) }
    var logoutPressed by remember { mutableStateOf(false) }
    val logoutScale by animateFloatAsState(
        targetValue = if (logoutPressed) 0.97f else 1f,
        animationSpec = tween(100)
    )
    val logoutInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(logoutInteractionSource) {
        logoutInteractionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> logoutPressed = true
                is PressInteraction.Release -> logoutPressed = false
                is PressInteraction.Cancel -> logoutPressed = false
            }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(viewModel.userName) }
    var editEmail by remember { mutableStateOf(viewModel.userEmail) }
    var editPassword by remember { mutableStateOf("") }
    var editConfirmPassword by remember { mutableStateOf("") }
    var editCurrentPassword by remember { mutableStateOf("") }
    var editError by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val bytes = stream.readBytes()
                    viewModel.uploadPhoto(bytes)
                }
            } catch (_: Exception) { }
        }
    }

    // Load photo bitmap from Appwrite URL
    val photoBitmap = remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(viewModel.photoFileId) {
        val fileId = viewModel.photoFileId ?: return@LaunchedEffect
        photoBitmap.value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val url = "https://nyc.cloud.appwrite.io/v1/storage/buckets/assets/files/$fileId/view?project=javaquiz"
                val connection = java.net.URL(url).openConnection()
                connection.setRequestProperty("Content-Type", "application/octet-stream")
                val inputStream = connection.getInputStream()
                inputStream.use { BitmapFactory.decodeStream(it) }
            } catch (_: Exception) { null }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = { AppBottomNavigationBar(navController = navController) }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoBitmap.value != null) {
                        Image(
                            bitmap = photoBitmap.value!!.asImageBitmap(),
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = viewModel.userName.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = viewModel.userName.ifEmpty { "Pengguna" },
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = viewModel.userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progres Belajar section
                Text(
                    text = "PROGRES BELAJAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats with top+bottom border
                val borderColor = MaterialTheme.colorScheme.outlineVariant
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            val strokeWidth = 1.dp.toPx()
                            val y = 0f
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                            val bottom = size.height
                            drawLine(
                                color = borderColor,
                                start = Offset(0f, bottom),
                                end = Offset(size.width, bottom),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.completedQuizzes.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Kuis",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.bestScore.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Skor",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Menu items
                ProfileMenuItem(
                    icon = Icons.Filled.Person,
                    title = "Edit Profil",
                    onClick = {
                        editName = viewModel.userName
                        editEmail = viewModel.userEmail
                        editPassword = ""
                        editConfirmPassword = ""
                        editCurrentPassword = ""
                        editError = ""
                        showEditDialog = true
                    },
                    showBorder = true
                )
                ProfileMenuItem(
                    icon = Icons.AutoMirrored.Filled.Help,
                    title = "Pusat Bantuan",
                    onClick = { showHelpDialog = true },
                    showBorder = true
                )
                ProfileMenuItem(
                    icon = Icons.Filled.Info,
                    title = "Tentang Aplikasi",
                    onClick = { showAboutDialog = true },
                    showBorder = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Logout
                Button(
                    onClick = {
                        isLoggingOut = true
                        viewModel.logout {
                            isLoggingOut = false
                            onLogout()
                        }
                    },
                    enabled = !isLoggingOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .scale(logoutScale),
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = logoutInteractionSource,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    )
                ) {
                    if (isLoggingOut) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Keluar",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Edit Profil dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    text = "Edit Profil",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Photo picker
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .clickable(enabled = !isUpdating) { photoPickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoBitmap.value != null) {
                            Image(
                                bitmap = photoBitmap.value!!.asImageBitmap(),
                                contentDescription = "Foto Profil",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = viewModel.userName.firstOrNull()?.uppercase() ?: "?",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Ganti Foto",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ketuk untuk ganti foto",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editPassword,
                        onValueChange = { editPassword = it },
                        label = { Text("Password Baru (opsional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editConfirmPassword,
                        onValueChange = { editConfirmPassword = it },
                        label = { Text("Konfirmasi Password Baru") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editCurrentPassword,
                        onValueChange = { editCurrentPassword = it },
                        label = { Text("Password Saat Ini") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdating,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (editError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = editError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (isUpdating) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editName.isBlank()) {
                            editError = "Nama tidak boleh kosong"
                            return@TextButton
                        }
                        if (editPassword.isNotEmpty() && editPassword != editConfirmPassword) {
                            editError = "Password baru tidak cocok"
                            return@TextButton
                        }
                        val changeEmail = editEmail.trim().takeIf { it != viewModel.userEmail }
                        val changePassword = editPassword.trim().takeIf { it.isNotEmpty() }
                        if (changeEmail == null && changePassword == null && editName.trim() == viewModel.userName) {
                            showEditDialog = false
                            return@TextButton
                        }
                        if ((changeEmail != null || changePassword != null) && editCurrentPassword.isBlank()) {
                            editError = "Masukkan password saat ini untuk mengubah email/password"
                            return@TextButton
                        }
                        editError = ""
                        isUpdating = true
                        viewModel.updateProfile(
                            newName = editName.trim(),
                            newEmail = changeEmail,
                            newPassword = changePassword,
                            currentPassword = editCurrentPassword
                        ) { success, emailError, passwordError ->
                            isUpdating = false
                            if (success) {
                                showEditDialog = false
                            } else {
                                editError = when {
                                    emailError && passwordError -> "Gagal mengubah email dan password"
                                    emailError -> "Gagal mengubah email"
                                    passwordError -> "Gagal mengubah password"
                                    else -> "Gagal menyimpan perubahan"
                                }
                            }
                        }
                    },
                    enabled = !isUpdating
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Pusat Bantuan dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "Pusat Bantuan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Cara menggunakan Java Quiz:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Pilih kategori kuis dari halaman Beranda",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "2. Jawab setiap pertanyaan dengan memilih salah satu opsi",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "3. Lihat hasil dan pembahasan setelah menyelesaikan kuis",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "4. Pantau perkembangan Anda di halaman Riwayat",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "5. Bandingkan skor dengan pengguna lain di Papan Peringkat",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Tentang Aplikasi dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Text(
                    text = "Tentang Aplikasi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Java Quiz",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Versi 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Aplikasi pembelajaran interaktif untuk menguasai pemrograman Java melalui kuis dan tantangan.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Fitur:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Kuis interaktif dengan berbagai kategori\n• Pembahasan jawaban setelah kuis\n• Papan peringkat secara langsung\n• Riwayat perkembangan belajar\n• Profil pengguna",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showBorder: Boolean
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (showBorder) Modifier.drawWithContent {
                    drawContent()
                    val strokeWidth = 1.dp.toPx()
                    val bottom = size.height
                    drawLine(
                        color = borderColor,
                        start = Offset(0f, bottom),
                        end = Offset(size.width, bottom),
                        strokeWidth = strokeWidth
                    )
                } else Modifier
            )
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

