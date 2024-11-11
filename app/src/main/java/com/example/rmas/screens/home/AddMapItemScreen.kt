package com.example.rmas.screens.home

import android.content.ContentResolver
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rmas.R
import com.example.rmas.models.MapItem
import com.example.rmas.models.User
import com.example.rmas.models.UserTag
import com.example.rmas.utility.ClearableImage
import com.example.rmas.viewModels.AddMapItemViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun AddMapItemScreen(
    visibility: MutableTransitionState<Boolean>,
    close: () -> Unit,
    newImageUriFlow: SharedFlow<Uri>,
    openCamera: () -> Unit,
    addMapItemViewModel: AddMapItemViewModel = viewModel(),
    author: User?,
    location: LatLng?,
    longLastingCoroutineScope: CoroutineScope,
    contentResolver: ContentResolver,
    startLoadingAnimation: () -> Unit,
    stopLoadingAnimation: () -> Unit,
    onNewMapItem: (MapItem) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AnimatedVisibility(
                visibility,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Scaffold(
                        modifier = Modifier
                            .systemBarsPadding(),
                        topBar = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 10.dp),
                                    onClick = close
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                                }

//                                Box(
//                                    modifier = Modifier.weight(1f)
//                                )

//                                Text(
//                                    modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
//                                    text = "Add an object to the map",
//                                    textAlign = TextAlign.Center,
//                                    fontSize = 6.em,
//                                    fontWeight = FontWeight.Light,
//                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        focusManager.clearFocus()
                                    })
                                }
                        ) {
                            author?.let { author ->
                                location?.let { location ->
                                    Content(close, innerPadding, newImageUriFlow, addImage = openCamera, addMapItemViewModel, author.uid, location, longLastingCoroutineScope, contentResolver, startLoadingAnimation, stopLoadingAnimation, onNewMapItem)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Content(
    close: () -> Unit,
    innerPadding: PaddingValues,
    newImageUriFlow: SharedFlow<Uri>,
    addImage: () -> Unit,
    addMapItemViewModel: AddMapItemViewModel = viewModel(),
    authorUid: String,
    location: LatLng,
    longLastingCoroutineScope: CoroutineScope,
    contentResolver: ContentResolver,
    startLoadingAnimation: () -> Unit,
    stopLoadingAnimation: () -> Unit,
    onNewMapItem: (MapItem) -> Unit,
) {
    val configuration = LocalConfiguration.current

    var isNewLaunch by rememberSaveable { mutableStateOf(true) }
    if (isNewLaunch) {
        addMapItemViewModel.resetTags()
        addMapItemViewModel.resetImages()

        isNewLaunch = false
    }

    LaunchedEffect(Unit) {
        newImageUriFlow.collect { uri ->
            addMapItemViewModel.addImage(uri)
        }
    }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        @Composable
        fun GroupScrollables() {
            Column {
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    modifier = Modifier.padding(horizontal = 16.dp) ,
//                    text = "Images",
//                    fontSize = 4.em,
//                    fontWeight = FontWeight.Light,
//                )

                ImageList(
                    images = addMapItemViewModel.images,
                    addImage = addImage,
                    removeImage = { index ->
                        addMapItemViewModel.removeImage(index)
                    }
                )

                Box(
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    SelectableTags(
                        addMapItemViewModel.selectedTags,
                        setTagValue = { id, value ->
                            addMapItemViewModel.setTagValue(id, value)
                        }
                    )
                }
            }
        }

        @Composable
        fun GroupInput() {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
                    ),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors().copy(
                        unfocusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = .6f),
                        focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = .1f)
                    ),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    singleLine = false,
                    maxLines = 3,
                    minLines = 3
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        longLastingCoroutineScope.launch {
                            try {
                                close()
                                startLoadingAnimation()
                                Log.i("map items", "map item upload about to start")
                                val newMapItem = addMapItemViewModel.uploadNewMapItem(
                                    addMapItemViewModel.images,
                                    addMapItemViewModel.selectedTags.values,
                                    title,
                                    description,
                                    authorUid,
                                    location,
                                    contentResolver,
                                )
                                stopLoadingAnimation()
                                onNewMapItem(newMapItem)
                                Log.i("map items", "map item upload successfully done")
                            } catch (err: Exception) {

                                stopLoadingAnimation()
                                Log.e("map items", "Error while creating new Map Item: $err.toString()")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ){
                    Text(text = "Done")
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Outlined.Done, "Done")
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                Row {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        GroupScrollables()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        GroupInput()
                    }
                }
            }
            else -> {
                GroupScrollables()
                GroupInput()
            }
        }
    }
}

@Composable
private fun ImageList(images: List<Uri>, addImage: () -> Unit, removeImage: (index: Int) -> Unit) {
    val roundness = RoundedCornerShape(10.dp)
    val style = Modifier
        .height(250.dp)
        .width(150.dp)
        .border(1.dp, MaterialTheme.colorScheme.outline, shape = roundness)
        .clip(roundness)

    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {

        Spacer(modifier = Modifier.width(16.dp))
        if (images.size < 5) {
            Button(
                modifier = style,
                shape = roundness,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                onClick = addImage
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = "Add image",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }

        images.forEachIndexed { index, uri ->
            ClearableImage(
                imageUri = uri,
                clear = { removeImage(index) },
                modifier = style
            )
            Spacer(modifier = Modifier.width(16.dp))
        }

        if (images.size < 2) {
            for(i in images.size..1) {
                Image(
                    painterResource(R.drawable.nature_placeholder),
                    "Fake image",
                    contentScale = ContentScale.Crop,
                    modifier = style
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }

    }
}

@Composable
fun SelectableTags(tags: Map<String, UserTag>, setTagValue: (id: String, value: Boolean) -> Unit) {
    Surface (
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            tags.values.forEach {
                Spacer(modifier = Modifier.width(16.dp))
                FilterChip(
                    onClick = {
                        setTagValue(it.tag.id, !it.selected)
                    },
                    label = {
                        Text(it.tag.name)
                    },
                    selected = it.selected,
                    shape = CircleShape,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        selectedContainerColor = MaterialTheme.colorScheme.background,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                    ),
                    leadingIcon = if (it.selected) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}