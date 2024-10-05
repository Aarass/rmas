package com.example.rmas.screens

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

@Composable
fun SignUp(
    navigateToSignInScreen: () -> Unit,
    openCamera: () -> Unit,
    newImageUriFlow: SharedFlow<Uri>,
    signUp: (name: String, surname: String, phoneNumber: String, email: String, password: String, imageUri: Uri) -> Unit,
    isSigningUpFlow: Flow<Boolean>,
) {
    val context = LocalContext.current

    var currentImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        newImageUriFlow.collect {
            currentImageUri = it
        }
    }

    val numericRegex = Regex("[^0-9]")

    var name by rememberSaveable { mutableStateOf("") }
    var surname by rememberSaveable { mutableStateOf("") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val isSigningUp by isSigningUpFlow.collectAsState(false)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Column(modifier = Modifier.fillMaxWidth(.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                ImageSelector(
                    currentImageUri,
                    openCamera = openCamera,
                    clear = { currentImageUri = null }
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    placeholder = { Text("John") },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") },
                    placeholder = { Text("Wick") },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = phoneNumber,
                    onValueChange = {
                        val stripped = numericRegex.replace(it, "")
                        phoneNumber = if (stripped.length >= 10) {
                            stripped.substring(0..9)
                        } else {
                            stripped
                        }
                    },
                    label = { Text("Phone number") },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("john@example.com") },
                    singleLine = true,
                    shape = RoundedCornerShape(99.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                )

                Button(
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 16.dp)
                        .fillMaxWidth(),
                    onClick = {
                        currentImageUri?.let { imageUri ->
                            // TODO(Validation)
                            signUp(name, surname, phoneNumber, email, password, imageUri)
                        } ?: Toast.makeText(context, "You must take picture", Toast.LENGTH_LONG).show()
                    })  {
                    Text(text = "Sign Up")
                }

                Row {
                    Text(text = "Already have an account? ")
                    Text(modifier = Modifier.clickable {
                        navigateToSignInScreen()
                    }, color = MaterialTheme.colorScheme.primary,text = "Sign In")
                }
            }
        }

        if (isSigningUp) {
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


@Composable
fun ImageSelector(imageUri: Uri?, openCamera: () -> Unit, clear: () -> Unit) {
    val roundedCornerSize = 24.dp
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(150.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(roundedCornerSize)
            )
            .clip(RoundedCornerShape(roundedCornerSize))
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .build(),
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            SmallFloatingActionButton(
                onClick = clear,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopEnd),
                containerColor = Color.White,
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(roundedCornerSize)
                    ),
                shape = RoundedCornerShape(roundedCornerSize),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = openCamera
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Person",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}