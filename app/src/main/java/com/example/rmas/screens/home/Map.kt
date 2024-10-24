package com.example.rmas.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.User
import com.example.rmas.models.UserTag
import com.example.rmas.ui.theme.resetSystemNavigationTheme
import com.example.rmas.ui.theme.setDarkStatusBarIcons
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun Map(
    innerPadding: PaddingValues,
    contentResolver: ContentResolver,
    locationClient: FusedLocationProviderClient,
    currentUser: User?,
    signOut: () -> Unit,
    tags: Map<String, UserTag>,
    setTag: (id: String, value: Boolean) -> Unit,
    isAddMapItemScreenVisible: Boolean,
    openAddMapItemScreen: (location: LatLng) -> Unit,
    closeAddMapItemScreen: () -> Unit,
) {
    val activity = (LocalContext.current as Activity)
    val window = activity.window

    val coroutineScope = rememberCoroutineScope()

    val map = remember { mutableStateOf<GoogleMap?>(null) }

    var profileDialogVisible  by remember { mutableStateOf(false) }

    val nis = LatLng(43.321445, 21.896104)

    val uiSettings  = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    }
    val cameraPositionState = rememberCameraPositionState()
    val properties = remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false,
            )
        )
    }

    fun enableMyLocation() {
        properties.value = properties.value.copy(
            isMyLocationEnabled = true
        )

        coroutineScope.launch {
            val location = locationClient.lastLocation.await()
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
        }
    }

    fun openProfileDialog() {
        profileDialogVisible = true
    }


    val locationPermissionState = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        if (isGranted) {
            enableMyLocation()
        }
    }

    LaunchedEffect(Unit) {
        if(locationPermissionState.status.isGranted) {
            enableMyLocation()
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties.value,
        uiSettings = uiSettings,
        contentPadding = WindowInsets.navigationBars.add(WindowInsets.statusBars).asPaddingValues(),
    ) {
        MapEffect {
            map.value = it
        }


        Marker(
            state = MarkerState(position = nis),
            title = "Nis",
            snippet = "Marker in Nis"
        )
    }

    fun setMapViewToMyLocation() {
        coroutineScope.launch {
            if (locationPermissionState.status.isGranted) {
                val location = locationClient.lastLocation.await()
                map.value?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            location.latitude,
                            location.longitude
                        ),
                        15f
                    )
                ) ?: Log.e(null, "Map is null")
            } else {
                locationPermissionState.launchPermissionRequest()
            }
        }
    }

    val state = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            skipHiddenState = false
        )
    )

    // UI Layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ovo je padding za navigaciju (na mapi nema ovoga jer zelim da se prikazuje ispod navigacije
            .padding(innerPadding)
    ) {
        BottomSheetScaffold(
            sheetPeekHeight = BottomSheetDefaults.SheetPeekHeight,
            containerColor = Color.Transparent,
            scaffoldState = state,
            sheetContent = {
                Text(
                    "Swipe up to open sheet. Swipe down to dismiss.",
                    modifier = Modifier.padding(16.dp, 100.dp).fillMaxSize()
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 0.dp, 0.dp, if(state.bottomSheetState.isVisible) BottomSheetDefaults.SheetPeekHeight else 0.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Surface(
                        modifier = Modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        shape = CircleShape,
                        shadowElevation = 10.dp,
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.5f.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape),
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "App logo image",
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = "Search author")
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(0.dp),
                                    onClick = {

                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.FilterAlt,
                                        contentDescription = "Filters icon",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentUser?.imageUrl)
                                        .placeholder(R.drawable.avatar)
                                        .error(R.drawable.no_image)
                                        .build(),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .clickable(
                                            indication = rememberRipple(bounded = true),
                                            interactionSource = remember { MutableInteractionSource() },
                                            onClick =  {
                                                openProfileDialog()
                                            }
                                        ),
                                )
                            }
                        }
                    }

                    val selected = remember { mutableStateListOf<UserTag>().apply {
                        this.addAll(tags.values)
                    } }

                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Spacer(modifier = Modifier.width(16.dp))
                        selected.forEachIndexed { index, it ->
                            FilterChip(
                                elevation = SelectableChipElevation(
                                    elevation = 10.dp,
                                    draggedElevation = 10.dp,
                                    focusedElevation = 10.dp,
                                    hoveredElevation = 10.dp,
                                    pressedElevation = 10.dp,
                                    disabledElevation = 10.dp,
                                ),
                                onClick = {
                                    selected[index] = it.copy(
                                        selected = !it.selected
                                    )
                                    setTag(it.tag.id, !it.selected)
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
                                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = it.selected, borderColor = Color.Transparent),
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

                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp).align(Alignment.End)
                ) {
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(bottom = 16.dp),

                        onClick = { setMapViewToMyLocation() },
                        shape = CircleShape,
//                containerColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = "My location icon",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                val location = locationClient.lastLocation.await()
                                openAddMapItemScreen(LatLng(location.latitude, location.longitude))
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }
            }
        }
    }

    if (profileDialogVisible) {
        ProfileDialog(currentUser, signOut) { profileDialogVisible = false }
    }

    DisposableEffect(LocalLifecycleOwner.current) {
        if (!isAddMapItemScreenVisible) {
            setDarkStatusBarIcons(window)
        }
        onDispose {
            resetSystemNavigationTheme(window)
        }
    }

    BackHandler(enabled = isAddMapItemScreenVisible) {
        closeAddMapItemScreen()
    }
}

@Composable
fun ProfileDialog(currentUser: User?, signOut: () -> Unit, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)

        Card(
            shape = RoundedCornerShape(140.dp, 140.dp, 30.dp, 30.dp),
            colors =  CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.background
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentUser?.imageUrl)
                        .placeholder(R.drawable.avatar)
                        .error(R.drawable.no_image)
                        .build(),
                    contentDescription = "",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${currentUser?.name}",
                    fontSize = 5.em,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${currentUser?.surname}",
                    fontSize = 5.em,
                    fontWeight = FontWeight.Light,
                )
                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        signOut()
                        onDismissRequest()
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error,
                        disabledContentColor = MaterialTheme.colorScheme.tertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiary
                    )
                ){
                    Text(text = "Sign Out")
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.AutoMirrored.Outlined.ExitToApp, "Sign out")
                }
            }
        }
    }
}