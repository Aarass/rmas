package com.example.rmas.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas.viewModels.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun SignIn(navigateToSignUpScreen: () -> Unit) {
    val viewModel = viewModel<AuthViewModel>()

    val coroutineScope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }




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

            Button(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp).fillMaxWidth(), onClick = {
                coroutineScope.launch {
                    viewModel.signIn(email, password)
                }
            }) {
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
}
