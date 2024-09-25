package com.example.rmas.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Home(
    signOut: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(50.dp)) {
        Text(text = "Homeee!")
        Button(onClick = signOut) {
            Text(text = "Sign out")
        }
    }
}
