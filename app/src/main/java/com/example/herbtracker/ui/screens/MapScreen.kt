package com.example.herbtracker.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.herbtracker.R
import com.example.herbtracker.data.HerbPoint
import com.example.inventory.ui.navigation.NavigationDestination
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

object MapDestination : NavigationDestination {
    override val route = "map_destination"
    override val titleRes = R.string.map_title
}

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mapPointUiState by mapViewModel.mapPointUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_small)),
        contentAlignment = Alignment.Center
    ){
        Card (
            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium))
        ){
            MyMap(markerList = mapPointUiState.pointList)
        }
        Column(modifier = Modifier
            .padding(dimensionResource(R.dimen.padding_medium))
            .align(Alignment.BottomStart)
        ){

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
            FloatingActionButton(
                onClick = { navigateToHome() },
                content = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.homescreen)
                    )
                }
            )

        }

    }
}

@Composable
fun MyMap(
    markerList: List<HerbPoint>,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = when(markerList.isNotEmpty()){
            true -> CameraPosition.fromLatLngZoom(
                LatLng(
                    markerList.last().latitude,
                    markerList.last().longitude),
                7f)
            false -> CameraPosition.fromLatLngZoom(
                LatLng(40.64,
                    22.94),
                7f)
        }
    }
    
    GoogleMap(
        modifier = Modifier,
        cameraPositionState = cameraPositionState,
    ) {
        if (markerList.isNotEmpty()) {
            LaunchedEffect(key1 = true) {
                delay(5_000L)
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newCameraPosition(
                        CameraPosition(LatLng(
                            markerList.last().latitude,
                            markerList.last().longitude),
                            7f, 0f, 0f)
                    ),
                    durationMs = 3000
                )
            }
            markerList.map { item ->
                Marker(
                    state = MarkerState(position = LatLng(item.latitude, item.longitude)),
                    title = item.name
                )
            }
        }

    }

}
