package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import com.dicoding.asclepius.ml.CancerClassification

class ImageClassifierHelper(private val context: Context) {

    // Initialize the TensorFlow Lite model
    private fun setupImageClassifier(): CancerClassification {
        return CancerClassification.newInstance(context)
    }

    // Classify the static image and return the result
    fun classifyStaticImage(image: Bitmap): Pair<String, Float> {
        val model = setupImageClassifier()

        // Prepare the image for the model (convert to TensorImage)
        val tensorImage = TensorImage.fromBitmap(image)

        // Run model inference
        val outputs = model.process(tensorImage)
        val probability = outputs.probabilityAsCategoryList

        // Get the prediction with the highest probability
        val topPrediction = probability.maxByOrNull { it.score }
        val prediction = topPrediction?.label ?: "Unknown"
        val confidenceScore = topPrediction?.score ?: 0f

        // Release model resources
        model.close()

        return Pair(prediction, confidenceScore)
    }
}
