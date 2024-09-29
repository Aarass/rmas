package com.example.rmas.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.rmas.R
import com.example.rmas.models.UserTag
import com.example.rmas.ui.theme.resetSystemNavigationTheme
import com.example.rmas.ui.theme.setDarkStatusBarIcons
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("MissingPermission")
@MapsComposeExperimentalApi
@ExperimentalPermissionsApi
@Composable
fun Map(
    innerPadding: PaddingValues,
    openProfile: () -> Unit,
    tags: Map<String, UserTag>,
    setTag: (id: String, value: Boolean) -> Unit,
) {
    val activity = (LocalContext.current as Activity)
    val window = activity.window

    val coroutineScope = rememberCoroutineScope()

    val locationClient = LocationServices.getFusedLocationProviderClient(activity)

    val map = remember { mutableStateOf<GoogleMap?>(null) }

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
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

                        Image(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable(
                                    indication = rememberRipple(bounded = true),
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    openProfile()
                                },
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "User image",
                        )
                    }
                }
            }

            val selected = remember { mutableStateListOf<UserTag>().apply {
                this.addAll(tags.values)
            } }

            Row(modifier = Modifier .horizontalScroll(rememberScrollState())) {
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
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 16.dp),

                onClick = { setMapViewToMyLocation() },
                shape = CircleShape,
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "My location icon",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            FloatingActionButton(
                onClick = { setMapViewToMyLocation() },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add icon",
                    tint = Color.White,
                )
            }

        }

    }

    DisposableEffect(LocalLifecycleOwner.current) {
        setDarkStatusBarIcons(window)
        onDispose {
            resetSystemNavigationTheme(window)
        }
    }
}