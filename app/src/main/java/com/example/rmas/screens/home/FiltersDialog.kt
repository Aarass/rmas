@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.rmas.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

@Preview
@Composable
fun tmp() {
    val width = 350
    Box(modifier = Modifier.width(width.dp).height(((16.0/9.0) * width).dp)) {
        FiltersDialog({}, {})
    }
}

@Composable
fun FiltersDialog(onConfirmation: () -> Unit, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)
        Card(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors =  CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {

            val users = listOf(
                User("123", "Aleksandar", "Prokopovic", "125812", "https://firebasestorage.googleapis.com/v0/b/rmas-436221.appspot.com/o/images%2F509da977-f839-4d76-a10e-24e69557a1a5.jpg?alt=media&token=a4e961ff-80ba-4bce-a8e5-1c4f64805921"),
                User("312", "Mika", "Peric", "125812", "https://firebasestorage.googleapis.com/v0/b/rmas-436221.appspot.com/o/images%2F509da977-f839-4d76-a10e-24e69557a1a5.jpg?alt=media&token=a4e961ff-80ba-4bce-a8e5-1c4f64805921")
            )


            var authorFullName by remember { mutableStateOf(null as String?)}
            var selectedAuthor by remember { mutableStateOf<User?>(users[0])}
            var authorsListIsExpanded by remember { mutableStateOf(true) }



            var dateRange by remember { mutableStateOf(Pair<Long?, Long?>(null, null))}

            var usedWord by remember { mutableStateOf(null as String?)}

            var sliderValue by remember { mutableStateOf(null as Float?) }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column {
                    Box {
                        TextField(
                            readOnly = selectedAuthor != null,
                            value = authorFullName ?: "",
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors().copy(
                                unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                                focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
                            ),
                            onValueChange = {
                                authorsListIsExpanded = it.isNotEmpty()
                                authorFullName = it
                            },
                            label = { Text("User's name or surname") },
                            singleLine = true,
                            suffix = {
                                if (authorFullName?.isNotEmpty() == true) {
                                    IconButton(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(0.dp),
                                        onClick = {
                                            authorFullName = null
                                            authorsListIsExpanded = false
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Filters icon",
                                            tint = MaterialTheme.colorScheme.secondary
                                        )
                                    }
                                }
                            }
                        )

                        Box (
                            modifier = Modifier.height(55.dp).padding(horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            selectedAuthor?.let { author ->
                                Card(
                                    colors = CardDefaults.cardColors().copy(
                                        containerColor = MaterialTheme.colorScheme.background
                                    ),
                                    shape = RoundedCornerShape(99.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        UserOption(user = author, select = {_ -> })
                                        IconButton(
                                            modifier = Modifier.size(30.dp).padding(0.dp).padding(end = 6.dp),
                                            onClick = {
                                                selectedAuthor = null
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Clear,
                                                contentDescription = "Filters icon",
                                                tint = MaterialTheme.colorScheme.secondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (authorsListIsExpanded) {
                        Column(
                            modifier = Modifier.clip(RoundedCornerShape(0.dp, 0.dp, 6.dp, 6.dp))
                        ) {
                            val selectAuthor = { author: User ->
                                selectedAuthor = author
                            }
                            users.forEachIndexed { i, user ->
                                if (i > 0) {
                                    HorizontalDivider()
                                }

                                UserOption(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = .1f))
                                        .fillMaxWidth(),
                                    user,
                                    selectAuthor
                                )
                            }
                        }
                    }
                }

                DateRange(dateRange, onValueChange = { newDateRange -> dateRange = newDateRange })

                TextField(
                    value = usedWord ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
                    ),
                    onValueChange = {
                        usedWord = it.ifEmpty { null }
                    },
                    label = { Text("Used keyword") },
                    singleLine = true,
                    suffix = {
                        if (usedWord?.isNotEmpty() == true) {
                            IconButton(
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(0.dp),
                                onClick = {
                                    usedWord = null
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Clear,
                                    contentDescription = "Filters icon",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                )

                var sliderPosition by remember { mutableFloatStateOf(1.0f) }
                Column {
                    Slider(
                        value = sliderPosition,
                        onValueChange = {
                            sliderPosition = it
                            sliderValue = 10f.pow(sliderPosition * log10(1000f))
                        },
                        valueRange = 0f..1f
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Text(text = "Max distance: ${ sliderValue?.let {"%.1f km".format(it)} ?: "∞"}")
                        TextButton(
                            onClick = {
                                sliderValue = null
                                sliderPosition = 1.0f
                            },
                            modifier = Modifier.padding(0.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Reset", fontSize = 10.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Dismiss")
                    }
                    TextButton(
                        onClick = { onConfirmation() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Confirm")
                    }
                }
            }


        }
    }
}


@Composable
fun UserOption(modifier: Modifier = Modifier, user: User, select: (User) -> Unit){
    Row(
        modifier = modifier
            .padding(vertical = 6.dp, horizontal = 6.dp)
            .clickable {
                select(user)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.imageUrl)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.no_image)
                .build(),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(30.dp).clip(CircleShape)
        )
        Text(
            modifier = Modifier.padding(start = 6.dp),
            text = "${user.name} ${user.surname}",
            color = MaterialTheme.colorScheme.onBackground,
        )

    }
}

@Composable
fun DateRange(
    value: Pair<Long?, Long?>,
    onValueChange: (Pair<Long?, Long?>) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    var showDatePicker by remember { mutableStateOf(false) }

    TextField(
        value = "${value.first?.let { convertMillisToDate(it) } ?: ""} - ${value.first?.let { convertMillisToDate(it) } ?: ""}",
        modifier = Modifier.fillMaxWidth().height(64.dp),
        readOnly = true,
        colors = TextFieldDefaults.colors().copy(
            unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
            focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
        ),
        onValueChange = {
        },
        trailingIcon = {
            IconButton(onClick = { showDatePicker = !showDatePicker }) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date"
                )
            }
        },
        label = { Text("Date Range") },
        singleLine = true,
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = {showDatePicker = false},
            confirmButton = {
                TextButton(onClick = {
                    onValueChange(Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    ))
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = dateRangePickerState)
        }

    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}