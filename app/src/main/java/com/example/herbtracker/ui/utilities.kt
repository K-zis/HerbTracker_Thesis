package com.example.herbtracker.ui

import android.graphics.Bitmap
import android.util.Log
import com.example.herbtracker.ml.Fhmodeldensenet121totalQuantF16
import com.example.herbtracker.ml.Fhmodelmobilenetv2QuantF16
import com.example.herbtracker.model.ACCEPTABLE_PROBABILITY_LIMIT
import com.example.herbtracker.model.IMAGE_SIZE
import com.example.herbtracker.model.PhotoItemUiState
import com.example.herbtracker.ui.home.HomeViewModel
import kotlinx.serialization.json.jsonObject
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp


fun modelInferenceDenseNet121(uiState: PhotoItemUiState, model: Fhmodeldensenet121totalQuantF16, viewModel: HomeViewModel) {

    // Creates a processor for preprocessing the input image for the model
    val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(IMAGE_SIZE, IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    // convert the image to appropriate bitmap for model
    val properBitmap = uiState.bitmap?.copy(Bitmap.Config.ARGB_8888, true)

    // loads the float array of the image and preprocess it
    var tfImage = TensorImage(DataType.FLOAT32)
    tfImage.load(properBitmap)
    tfImage = imageProcessor.process(tfImage)

    // Runs model inference and gets result sorted descending by probability
    val outputs = model.process(tfImage.tensorBuffer).probabilityAsCategoryList.apply {
        sortByDescending { it.score }
    }

    // update the state depending the result
    var label = outputs[0].label
    if (outputs[0].score.toDouble() < ACCEPTABLE_PROBABILITY_LIMIT) {
        label = ""
    }
    viewModel.updateNameAndProbability(label, outputs[0].score.toDouble())

    if (uiState.canNavigateToMap) {
        viewModel.updateCanNavigateToMap(outputs[0].score.toDouble() > ACCEPTABLE_PROBABILITY_LIMIT)
    }
    model.close()

}

fun modelInferenceMobileNetV2(uiState: PhotoItemUiState, model: Fhmodelmobilenetv2QuantF16, viewModel: HomeViewModel) {

// Creates inputs for reference.
    val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(IMAGE_SIZE, IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    val properBitmap = uiState.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
    var tfImage = TensorImage(DataType.FLOAT32)
    tfImage.load(properBitmap)
    tfImage = imageProcessor.process(tfImage)


// Runs model inference and gets result.
    val outputs = model.process(tfImage.tensorBuffer).probabilityAsCategoryList.apply {
        sortByDescending { it.score }
    }

    var label = outputs[0].label
    if (outputs[0].score.toDouble() < ACCEPTABLE_PROBABILITY_LIMIT) {
        label = ""
    }
    viewModel.updateNameAndProbability(label, outputs[0].score.toDouble())

    if (uiState.canNavigateToMap) {
        viewModel.updateCanNavigateToMap(outputs[0].score.toDouble() > ACCEPTABLE_PROBABILITY_LIMIT)
    }
    model.close()

}

suspend fun getWikiDescription(imageName: String, homeViewModel: HomeViewModel): String {
    var result = homeViewModel.getWikiRequest(imageName)

    var response = result.entries.map {
        Pair(it.key, it.value)
        it.toPair()
    }
    var query =  response.first {
        it.first == "query"
    }
    var pages = query.second.jsonObject["pages"]?.jsonObject
    var pageIdPair = pages?.entries?.map {
        Pair(it.key, it.value)
        it.toPair()
    }
    val pageIdValue = pageIdPair?.
    first()?.
    second?.
    jsonObject?.
    get("pageid").toString()

    if (pageIdValue == "null") {
        val imageNameCut = imageName.split("\\s+".toRegex())[0]
        result = homeViewModel.getWikiRequest(imageNameCut)

        response = result.entries.map {
            Pair(it.key, it.value)
            it.toPair()
        }
        query =  response.first {
            it.first == "query"
        }
        pages = query.second.jsonObject["pages"]?.jsonObject
        pageIdPair = pages?.entries?.map {
            Pair(it.key, it.value)
            it.toPair()
        }
    }
    val extract = pageIdPair?.
                    first()?.
                    second?.
                    jsonObject?.
                    get("extract").
                    toString().
                    replace("\\n", "").
                    replace("\\\"","\"")

    return extract.substring(1, extract.length - 1)
}

