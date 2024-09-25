package com.example.rmas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas.viewModels.AuthViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun SignIn(
    navigateToSignUpScreen: () -> Unit,
    signIn: (email: String, password: String) -> Unit,
    isSigningInFlow: Flow<Boolean>,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isSigningIn by isSigningInFlow.collectAsState(false);

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Column(modifier = Modifier.fillMaxWidth(.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("john@example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "Email Icon")
                    },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(99.dp),
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = "Email Icon")
                    },
                )

                Button(
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        signIn(email, password)
                    }
                ) {
                    Text(text = "Sign In")
                }

                Row {
                    Text(text = "Don't have an account? ")
                    Text(
                        modifier = Modifier.clickable { navigateToSignUpScreen() },
                        color = MaterialTheme.colorScheme.primary,
                        text = "Sign Up"
                    )
                }
            }
        }

        if (isSigningIn) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color(0f, 0f, 0f, 0.5f))
                .pointerInput(Unit) {}
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
