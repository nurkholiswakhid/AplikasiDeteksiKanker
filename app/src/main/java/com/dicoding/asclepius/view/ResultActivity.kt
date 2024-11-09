package com.dicoding.asclepius.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.ml.CancerClassification
import org.tensorflow.lite.support.image.TensorImage

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the image byte array passed from MainActivity
        val byteArray = intent.getByteArrayExtra("IMAGE_BYTE_ARRAY")
        if (byteArray != null) {
            // Convert byte array back to Bitmap
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            // Display the image in the ImageView
            binding.resultImage.setImageBitmap(bitmap)

            // Process the image to make predictions
            val resultText = classifyImage(bitmap)
            binding.resultText.text = resultText
        } else {
            binding.resultText.text = "Gambar tidak tersedia"
        }
    }

    private fun classifyImage(bitmap: Bitmap): String {
        // Convert Bitmap to TensorImage
        val tensorImage = TensorImage.fromBitmap(bitmap)

        // Load the model
        val model = CancerClassification.newInstance(this)

        // Perform inference
        val outputs = model.process(tensorImage)
        val probability = outputs.probabilityAsCategoryList

        // Determine the label with the highest confidence
        val topResult = probability.maxByOrNull { it.score }

        // Check if "Cancer" category is the result
        return if (topResult != null) {
            val categoryName = topResult.label
            val confidence = (topResult.score * 100).toInt()  // Convert to percentage
            if (categoryName == "Cancer") {
                "CANCER: $confidence%"
            } else {
                "NON CANCER: $confidence%"
            }
        } else {
            "Hasil tidak ditemukan"
        }.also {
            model.close()  // Release resources
        }
    }
}
