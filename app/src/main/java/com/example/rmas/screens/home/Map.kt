package com.example.rmas.screens.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.location.Location
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.FilterAlt
import androidx.compose.material.icons.rounded.KeyboardDoubleArrowUp
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rmas.R
import com.example.rmas.models.Filters
import com.example.rmas.models.MapItem
import com.example.rmas.models.User
import com.example.rmas.ui.theme.resetSystemNavigationTheme
import com.example.rmas.ui.theme.setDarkStatusBarIcons
import com.example.rmas.viewModels.FiltersViewModel
import com.example.rmas.viewModels.PointsViewModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.math.abs

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
    isAddMapItemScreenVisible: Boolean,
    openAddMapItemScreen: (location: LatLng) -> Unit,
    closeAddMapItemScreen: () -> Unit,
    mapItems: List<MapItem>,
    queryMapItems: (Filters, Location) -> Unit,
    selectedMapItem: MapItem?,
    selectMapItem: (MapItem) -> Unit,
    deselectMapItem: () -> Unit,
    filtersViewModel: FiltersViewModel,
) {
    val activity = (LocalContext.current as Activity)
    val window = activity.window

    val coroutineScope = rememberCoroutineScope()

    val map = remember { mutableStateOf<GoogleMap?>(null) }

    var profileDialogVisible  by remember { mutableStateOf(false) }
    var filtersDialogVisible by remember { mutableStateOf(false) }

    val uiSettings  = remember {
        MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        )
    }
    val cameraPositionState = rememberCameraPositionState()

    selectedMapItem?.let { item ->
        cameraPositionState.position =
            CameraPosition.fromLatLngZoom(LatLng(item.location.latitude, item.location.longitude), 15f)
    }
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
        onMapClick = {
            Log.i("asd", "map click")
        }
    ) {
        MapEffect {
            map.value = it
        }


        mapItems.forEach { item ->
            Marker(
                state = MarkerState(position = item.location),
//                icon = BitmapDescriptorFactory.fromResource(R.drawable.marker),
//                anchor = Offset(0.0f, 1.0f),
                onClick = {
                    deselectMapItem()
                    selectMapItem(item)

                    // Keep true
                    true
                }
            )
        }
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
        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
    )

    Log.i("asd", "${state.bottomSheetState.currentValue} ${state.bottomSheetState.targetValue}")

//    var isNewLaunch by rememberSaveable { mutableStateOf(true) }


//    if (state.bottomSheetState.currentValue == state.bottomSheetState.targetValue) {
//        if (state.bottomSheetState.currentValue == SheetValue.Hidden) {
    if (state.bottomSheetState.currentValue != SheetValue.Hidden) {
        if (state.bottomSheetState.targetValue == SheetValue.Hidden) {
            Log.i("asd", "clearam")
            deselectMapItem()
        }
    }

//    LaunchedEffect(selectedMapItem?.id) {
//        if (selectedMapItem != null) {
//            launch {
//                state.bottomSheetState.show()
//            }
//        }
//    }

    val onFiltersChanged = {
        coroutineScope.launch {
            val location = locationClient.lastLocation.await()
            val filters = filtersViewModel.currentFilters
            queryMapItems(filters, location)
        }
    }

    // UI Layer
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Ovo je padding za navigaciju (na mapi nema ovoga jer zelim da se prikazuje ispod navigacije
            .padding(innerPadding)
    ) {
        fun Modifier.ignoreNextModifiers(): Modifier {
            return object : Modifier by this {
                override fun then(other: Modifier): Modifier {
                    return this
                }
            }
        }

        BottomSheetScaffold(
            // Hack. Scaffold blokira touch iz sebe, a google mapa je iza njega
            modifier = Modifier.fillMaxSize().ignoreNextModifiers(),
//            sheetPeekHeight = BottomSheetDefaults.SheetPeekHeight * 2,
            sheetPeekHeight = if (selectedMapItem == null) 0.dp
                else BottomSheetDefaults.SheetPeekHeight * 3,
            containerColor = Color.Transparent,
            scaffoldState = state,
            sheetContent = {
                Log.i("asd", "item: $selectedMapItem")
                selectedMapItem?.let { item ->
                    Column(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxHeight()
                    ) {
                        MapItemPreview(
                            item,
                            currentUser = currentUser,
                            getTagById = { id ->
                                filtersViewModel.getTagById(id)
                            },
                        )
//                        getUserById = { id -> User(id, "Aleksandar", "Prokopovic", "0621715606", "") }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
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
                                Text(text = "Hello, ${currentUser?.fullName ?: ""}")
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .padding(0.dp),
                                    onClick = {
                                        filtersDialogVisible = true
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
                                        .clickable {
                                            openProfileDialog()
                                        }
                                )
                            }
                        }
                    }

                    // Hack ali radi
                    LaunchedEffect(filtersViewModel.userTags.size) {
                        if (filtersViewModel.userTags.isNotEmpty()) {
                            onFiltersChanged()
                        }
                    }

                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Spacer(modifier = Modifier.width(16.dp))
                        filtersViewModel.userTags.values.forEachIndexed { index, it ->
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
                                    filtersViewModel.setTagValue(it.tag.id, !it.selected)
                                    onFiltersChanged()
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

    if (filtersDialogVisible) {
        FiltersDialog(
            onConfirmation = { author, dataRange, distance ->
                filtersViewModel.setFilters(author, dataRange, distance)
                filtersDialogVisible = false
                onFiltersChanged()
            },
            onDismissRequest = {
                filtersDialogVisible = false
            },
            filtersViewModel = filtersViewModel,
        )
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
    var pointsVisible by rememberSaveable { mutableStateOf(false) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)

        Card(
//            shape = RoundedCornerShape(140.dp, 140.dp, 30.dp, 30.dp),
            shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp),
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

                currentUser?.let { user ->
                    val parts = user.fullName.split(" ", limit = 2)
                    Text(
                        text = parts[0],
                        fontSize = 5.em,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = parts[1],
                        fontSize = 5.em,
                        fontWeight = FontWeight.Light,
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    Spacer(modifier = Modifier.width(16.dp))
                    IconButton(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(0.dp),
                        onClick = {
                            pointsVisible = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = "Points icon",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        currentUser?.let { user ->
            if (pointsVisible) {
                Dialog(onDismissRequest = { pointsVisible = false }) {
                    (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0.2f)

                    Card(
                        shape = RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp),
                        colors = CardDefaults.cardColors().copy(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                    ) {
                        Box(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            PointsList(user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PointsList(user: User, pointsViewModel: PointsViewModel = viewModel()) {
    LaunchedEffect(user.uid) {
        pointsViewModel.getPointsForUser(user.uid)
    }

    LazyColumn {
        items(pointsViewModel.points) { item ->
            BadgedBox(
                modifier = Modifier.padding(top = 6.dp),
                badge = {
                    Badge() {
                        Text("${if (item.value >= 0) "+" else "-"}${abs(item.value)}")
                    }
                }
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors().copy(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
//                        containerColor = Color(0xFFFFAF42),
                    )
                ) {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = item.message
                    )
                }
            }
        }
    }
}