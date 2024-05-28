package com.example.herbtracker.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.herbtracker.R
import com.example.herbtracker.ui.ComposeFileProvider
import com.example.herbtracker.ui.HerbAppViewModelProvider
import com.example.herbtracker.ui.screens.MapViewModel
import com.example.herbtracker.ui.theme.HerbTrackerTheme
import com.example.inventory.ui.navigation.NavigationDestination
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay


object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    navigateToClassification: () -> Unit,
    navigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapPointUiState by mapViewModel.mapPointUiState.collectAsState()


    var hasImage by remember {
        mutableStateOf(false)
    }


    var hasLocationPermission by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }

    val openAlertDialog = remember {
        mutableStateOf(false)
    }

    // Homescreen mini map initializations and preferences
    val cameraPositionState = rememberCameraPositionState {
        position = when(mapPointUiState.pointList.isNotEmpty()){
            true -> CameraPosition.fromLatLngZoom(
                LatLng(
                    mapPointUiState.pointList.last().latitude,
                    mapPointUiState.pointList.last().longitude),
                12f)
            false -> CameraPosition.fromLatLngZoom(
                LatLng(40.64,
                    22.94),
                12f)
        }
    }

    val mapOptions = MapUiSettings(
        zoomControlsEnabled = false,
        zoomGesturesEnabled = false,
        rotationGesturesEnabled = false,
        scrollGesturesEnabled = false,
        myLocationButtonEnabled = false,
        scrollGesturesEnabledDuringRotateOrZoom = false
        )

    // Launchers and actions accordingly
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                hasImage = imageUri != null
                imageUri = it
                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {

                        bitmap.value = MediaStore.Images
                            .Media.getBitmap(context.contentResolver,it)

                    } else {
                        val source = ImageDecoder
                            .createSource(context.contentResolver,it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let {  btm ->
                        viewModel.updateFromGallery(btm)

                        navigateToClassification()
                    }
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {success ->
            hasImage = success

        }
    )

    if (hasImage && photoUri != null){
        photoUri?.let {
            if (Build.VERSION.SDK_INT < 28) {

                bitmap.value = MediaStore.Images
                    .Media.getBitmap(context.contentResolver, it)

            } else {
                val source = ImageDecoder
                    .createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }

            bitmap.value?.let { btm ->

                when (viewModel.netUiState) {
                    is NetUiState.Success -> {
                        viewModel.updateFromCamera(btm)
                        navigateToClassification()
                    }
                    is NetUiState.Error -> {
                        openAlertDialog.value = true
                        viewModel.updateState(NetUiState.Loading)
                    }
                    is NetUiState.Loading -> {
                        bitmap.value = null
                        photoUri = null
                        hasImage = false
                    }
                }
            }
        }
    }

    // Dialog box decision
    when {
        openAlertDialog.value -> {
            NetAlertDialog(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                }
            )
        }
    }

    // Get permissions and the actual location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getDeviceLocation(fusedLocationProviderClient)
                hasLocationPermission = true
            }
        }

    fun askPermissions() = when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
            hasLocationPermission = true
        }
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier
        .paint(
            painter = painterResource(R.drawable.test),
            alpha = 0.8f,
            contentScale = ContentScale.Crop
    )){
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Text(text = "", modifier = Modifier.weight(0.5f))
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                Card(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_small)),
                    modifier = Modifier
                        .weight(0.25f)
                        .alpha(0.75f)
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)).weight(1f)
                    ) {

                        Text(
                            text = stringResource(R.string.welcome_screen),
                            style = MaterialTheme.typography.headlineSmall
                        )
//                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                        Text(
                            text = stringResource(R.string.motivation_moto),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
                Text(text = "", modifier = Modifier.weight(0.25f))
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Card(
                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_small)),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                        .weight(1f)
                ) {
                    Text(
                        text = "Recent history: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    )
                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = mapOptions
                    ) {
                        if (mapPointUiState.pointList.isNotEmpty()) {
                            LaunchedEffect(key1 = true) {
                                delay(5_000L)
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newCameraPosition(
                                        CameraPosition(
                                            LatLng(
                                                mapPointUiState.pointList.last().latitude,
                                                mapPointUiState.pointList.last().longitude
                                            ),
                                            10f, 0f, 0f
                                        )
                                    ),
                                    durationMs = 3000
                                )
                            }
                            if (mapPointUiState.pointList.size < 3) {
                                mapPointUiState.pointList.map { item ->
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(
                                                item.latitude,
                                                item.longitude
                                            )
                                        ),
                                        title = item.name
                                    )
                                }
                            } else {
                                mapPointUiState.pointList.takeLast(3).reversed().map { item ->
                                    Marker(
                                        state = MarkerState(
                                            position = LatLng(
                                                item.latitude,
                                                item.longitude
                                            )
                                        ),
                                        title = item.name
                                    )
                                }
                            }
                        }

                    }
                }
            }
            Row(modifier = Modifier.weight(0.25f, false)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.padding_medium)
                    )
                ) {
                    Row() {
                        SelectActionButton(
                            labelResourceId = R.string.btn_camera,
                            contentImage = R.string.camera_icon,
                            icon = Icons.Default.Camera,
                            onClick = {
                                fusedLocationProviderClient =
                                    LocationServices.getFusedLocationProviderClient(context)
                                askPermissions()
                                if (hasLocationPermission) {
                                    val uri = ComposeFileProvider.getImageUri(context)
                                    photoUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

                        SelectActionButton(
                            labelResourceId = R.string.btn_gallery,
                            contentImage = R.string.gallery_icon,
                            icon = Icons.Default.Image,
                            onClick = {
                                galleryLauncher.launch("image/*")
                            }
                        )
                    }
                    SelectActionButton(
                        labelResourceId = R.string.btn_map,
                        contentImage = R.string.map_icon,
                        icon = Icons.Default.Map,
                        onClick = navigateToMap
                    )
                }
            }
        }
    }


}

@Composable
fun SelectActionButton(
    @StringRes labelResourceId: Int,
    @StringRes contentImage: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
){

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
    ) {

        Row(){
            Icon(
                imageVector = icon,
                contentDescription = stringResource(contentImage)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

            Text(text=stringResource(labelResourceId), style = MaterialTheme.typography.bodyLarge)
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {

    AlertDialog(
        icon = {
            Icon(Icons.Filled.Info, contentDescription = "Example Icon")
        },
        text = {
            Text(text = "Something went wrong with retrieving the location.\nPlease check the internet connection.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Ok")
            }
        }
    )
}



@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HerbTrackerTheme {
        HomeScreen(
            viewModel = viewModel(),
            mapViewModel = viewModel(factory = HerbAppViewModelProvider.Factory),
            navigateToClassification = { },
            navigateToMap = { /*TODO*/ })
    }

}