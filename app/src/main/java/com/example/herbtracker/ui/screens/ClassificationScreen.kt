package com.example.herbtracker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.herbtracker.R
import com.example.herbtracker.ml.Fhmodeldensenet121totalQuantF16
import com.example.herbtracker.ml.Fhmodelmobilenetv2QuantF16
import com.example.herbtracker.model.ACCEPTABLE_PROBABILITY_LIMIT
import com.example.herbtracker.ui.home.HomeViewModel
import com.example.herbtracker.ui.home.SelectActionButton
import com.example.herbtracker.ui.modelInferenceDenseNet121
import com.example.herbtracker.ui.modelInferenceMobileNetV2
import com.example.inventory.ui.navigation.NavigationDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

object ClassificationDestination : NavigationDestination {
    override val route = "classification_destination"
    override val titleRes = R.string.classification_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassificationScreen(
    homeViewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    classificationViewModel: ClassificationViewModel,
    navigateBack: () -> Unit,
    navigateToHome: () -> Unit,
    navigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {

    val photoUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var menuExpanded by remember {
        mutableStateOf(false)
    }

    var photoFromCamera by remember {
        mutableStateOf(photoUiState.canNavigateToMap)
    }

    var modelName by remember {
        mutableStateOf("DenseNet-121")
    }

    val context = LocalContext.current

    val df = DecimalFormat("#.###")
    df.roundingMode = RoundingMode.CEILING

    BackHandler {
        classificationViewModel.updateModelVersionState(ModelVersionState.DenseNet121)
        navigateBack()
    }


    if (classificationViewModel.modelVersionState == ModelVersionState.DenseNet121) {
        val model = Fhmodeldensenet121totalQuantF16.newInstance(context)

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                modelInferenceDenseNet121(photoUiState, model, homeViewModel)
                delay(2_000L)
                if (photoUiState.imageName != "") {
                    classificationViewModel.retrieveNetData(homeViewModel, photoUiState)
                }
            }
        }
    } else if (classificationViewModel.modelVersionState == ModelVersionState.MobileNetV2) {
        val model = Fhmodelmobilenetv2QuantF16.newInstance(context)

        LaunchedEffect(Unit) {
            launch(Dispatchers.IO) {
                modelInferenceMobileNetV2(photoUiState, model, homeViewModel)
                delay(3_000L)
                if (photoUiState.imageName != "") {
                    classificationViewModel.retrieveNetData(homeViewModel, photoUiState)
                }
            }
        }
    }


    Box(modifier = Modifier
        .paint(
            painter = painterResource(R.drawable.test),
            alpha = 0.8f,
            contentScale = ContentScale.Crop
        )

    ){

        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopAppBar(
                title = { Text(text = "")},
                actions = {
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Default.MoreVert, "")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        // 6
                        DropdownMenuItem(
                            text = {
                                Text("MobileNetV2")
                            },
                            onClick = { classificationViewModel
                                .updateModelVersionState(ModelVersionState.MobileNetV2)
                                modelName = "MobileNetV2"
                                menuExpanded = false },
                        )
                        DropdownMenuItem(
                            text = {
                                Text("DenseNet121")
                            },
                            onClick = { classificationViewModel
                                .updateModelVersionState(ModelVersionState.DenseNet121)
                                modelName = "DenseNet-121"
                                menuExpanded = false },
                        )

                    }
                },
                modifier = Modifier.weight(0.05f))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.padding_small))
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                if (photoUiState.bitmap != null) {

                    Image(
                        bitmap = photoUiState.bitmap!!.asImageBitmap(),
                        contentDescription = when (photoUiState.probability > ACCEPTABLE_PROBABILITY_LIMIT) {
                            true -> photoUiState.imageName
                            false -> "Unknown"
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(400.dp)
                            .shadow(1.dp, RoundedCornerShape(10.dp))
                    )
                    if (photoUiState.probability > ACCEPTABLE_PROBABILITY_LIMIT) {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = dimensionResource(R.dimen.padding_small)
                            ),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(R.dimen.padding_small),
                                    end = dimensionResource(R.dimen.padding_small)
                                )
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = "The prediction result of $modelName model is:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(
                                    dimensionResource(R.dimen.padding_small)
                                )
                            )
                            Text(
                                text = photoUiState.imageName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Text(
                                text = "with confidence of ${
                                    df.format(photoUiState.probability * 100.0)
                                }%",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(
                                    dimensionResource(R.dimen.padding_small)
                                )
                            )
                        }
                        when (classificationViewModel.loadedUiState) {
                            is LoadedUiState.Success -> OnSuccessCard(
                                description = photoUiState.imageDescription, modifier = Modifier
                                    .padding(dimensionResource(R.dimen.padding_small))
                                    .align(Alignment.CenterHorizontally)
                                    .heightIn(0.dp, 250.dp)
                            )

                            is LoadedUiState.Loading -> Image(
                                painter = painterResource(R.drawable.loading_img),
                                contentDescription = "Loading...",
                                modifier = Modifier.size(width = 200.dp, height = 200.dp)
                            )

                            is LoadedUiState.Error -> {
                                Image(
                                    painter = painterResource(R.drawable.ic_connection_error),
                                    contentDescription = "Internet Error",
                                    modifier = Modifier.size(width = 100.dp, height = 100.dp)
                                )
                                Card(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = dimensionResource(R.dimen.padding_small)
                                    ),
                                    shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                                    modifier = Modifier
                                        .padding(
                                            start = dimensionResource(R.dimen.padding_small),
                                            end = dimensionResource(R.dimen.padding_small)
                                        )
                                        .align(Alignment.CenterHorizontally)
                                ) {
                                    Text(text = "Connection Error.", modifier = Modifier.padding(
                                        dimensionResource(R.dimen.padding_small)))
                                }
                            }
                        }


                    } else {
                        Card(
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = dimensionResource(R.dimen.padding_small)
                            ),
                            shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
                            modifier = Modifier
                                .padding(
                                    start = dimensionResource(R.dimen.padding_small),
                                    end = dimensionResource(R.dimen.padding_small)
                                )
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = stringResource(R.string.low_propability_text),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(
                                    dimensionResource(R.dimen.padding_small)
                                )
                            )
                        }
                    }

                }

            }
            Row(modifier = Modifier.weight(0.1f, false)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.padding_medium)
                    )
                ) {
                    Row() {
                        SelectActionButton(
                            labelResourceId = R.string.home,
                            contentImage = R.string.homescreen,
                            icon = Icons.Default.Home,
                            onClick = {
                                navigateToHome()
                                homeViewModel.updateDescription("")
                                classificationViewModel.updateLoadState(LoadedUiState.Loading)
                                classificationViewModel.updateModelVersionState(ModelVersionState.DenseNet121)

                            }
                        )
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))

                        SelectActionButton(
                            labelResourceId = R.string.add_to_map,
                            contentImage = R.string.map_icon,
                            icon = Icons.Default.AddLocationAlt,
                            enabled = photoUiState.canNavigateToMap,
                            onClick = {

                                coroutineScope.launch {
                                    mapViewModel.updateUiState(
                                        HerbItemDetails(
                                            name = photoUiState.imageName,
                                            latitude = photoUiState.latitude,
                                            longitude = photoUiState.longitude
                                        )
                                    )
                                    mapViewModel.saveItem()
                                    navigateToMap()
                                }

                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun OnSuccessCard(description: String, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.padding_small)
        ),
        shape = RoundedCornerShape(dimensionResource(R.dimen.padding_medium)),
        modifier = modifier
    ){
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    dimensionResource(R.dimen.padding_small)
                )
        )
    }
}