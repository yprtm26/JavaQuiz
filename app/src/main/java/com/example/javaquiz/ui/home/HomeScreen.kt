package com.example.javaquiz.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Halo, Developer!", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Siap mengasah kemampuan Java hari ini?")
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Kategori Kuis", style = MaterialTheme.typography.titleLarge)
        // Add more UI elements here based on the design
    }
}
