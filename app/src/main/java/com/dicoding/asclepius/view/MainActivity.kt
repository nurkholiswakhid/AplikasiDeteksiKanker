package com.dicoding.asclepius.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.viewmodel.ImageViewModel
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val imageViewModel: ImageViewModel by viewModels()
    private var currentBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore image if available
        imageViewModel.imageUri?.let { uri ->
            currentBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            binding.previewImageView.setImageBitmap(currentBitmap)
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            if (currentBitmap != null) {
                analyzeImage()
            } else {
                showToast("Pilih gambar terlebih dahulu!")
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            val uri = data?.data
            imageViewModel.imageUri = uri  // Save URI in ViewModel
            uri?.let {
                currentBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                binding.previewImageView.setImageBitmap(currentBitmap)
            }
        }
    }

    private fun analyzeImage() {
        moveToResult()
    }

    private fun moveToResult() {
        currentBitmap?.let { bitmap ->
            val bitmapData = compressBitmap(bitmap)
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("IMAGE_BYTE_ARRAY", bitmapData)
            startActivity(intent)
        }
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        return stream.toByteArray()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_CODE_GALLERY = 100
    }
}
