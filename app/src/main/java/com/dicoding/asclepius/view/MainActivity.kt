package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle gallery button click
        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        // Handle analyze button click
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let { uri ->
                val bitmap = getBitmapFromUri(uri)
                bitmap?.let {
                    analyzeImage(it)
                }
            } ?: showToast("Please select an image first!")
        }
    }

    // Start gallery activity to pick an image
    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryResultLauncher.launch(intent)
    }

    // Launch the gallery and receive result
    private val galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let {
                currentImageUri = it
                showImage(it)
            }
        }
    }

    // Display the image selected from gallery
    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
    }

    // Analyze the selected image using ImageClassifierHelper
    private fun analyzeImage(image: Bitmap) {
        val classifierHelper = ImageClassifierHelper(this)
        val result = classifierHelper.classifyStaticImage(image)
        val prediction = result.first
        val confidenceScore = result.second

        moveToResult(image, prediction, confidenceScore)
    }

    // Navigate to result screen and display prediction results
    private fun moveToResult(image: Bitmap, prediction: String, confidenceScore: Float) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("PREDICTION", prediction)
        intent.putExtra("CONFIDENCE_SCORE", confidenceScore)
        intent.putExtra("IMAGE_BITMAP", image) // Pass the bitmap image
        startActivity(intent)
    }

    // Show a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Helper method to convert Uri to Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        val inputStream = contentResolver.openInputStream(uri)
        return BitmapFactory.decodeStream(inputStream)
    }
}
