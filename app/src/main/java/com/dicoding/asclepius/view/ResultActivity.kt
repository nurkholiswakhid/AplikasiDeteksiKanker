package com.dicoding.asclepius.view

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Get the prediction, confidence score, and the image (Bitmap) from the intent
        val prediction = intent.getStringExtra("PREDICTION") ?: "Unknown"
        val confidenceScore = intent.getFloatExtra("CONFIDENCE_SCORE", 0f)
        val imageBitmap = intent.getParcelableExtra<Bitmap>("IMAGE_BITMAP") // Retrieve the image

        // Display the results
        val resultImageView: ImageView = findViewById(R.id.result_image)
        val resultTextView: TextView = findViewById(R.id.result_text)

        // Set the image to the ImageView
        resultImageView.setImageBitmap(imageBitmap)

        // Format the confidence score as a percentage
        val confidencePercentage = (confidenceScore * 100).toInt()

        // Set the text result (prediction and confidence score in percentage)
        resultTextView.text = "Prediction: $prediction\nConfidence Score: $confidencePercentage%"
    }
}
