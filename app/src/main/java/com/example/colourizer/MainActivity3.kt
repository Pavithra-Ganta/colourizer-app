package com.example.colourizer

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.colourizer.databinding.ActivityMain3Binding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Use view binding to inflate the layout
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra("colorized_image_path")
        imagePath?.let { path ->
            val imageFile = File(path)
            if (imageFile.exists()) {
                // Load the image into an ImageView (for example)
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                val imageView = findViewById<ImageView>(R.id.imageView4) // replace with your ImageView id
                imageView.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Image not found!", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "No image path received.", Toast.LENGTH_SHORT).show()
        }

        // Apply system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize toolbar and menu button
        toolbar = findViewById(R.id.toolbar)
        menuButton = findViewById(R.id.menuButton)

        // Set up the app bar and menu button using MenuHandler
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupMenu(this, menuButton)

        // Display the colorized image
        val colorizedImageUri = intent.getStringExtra("colorized_image_path")
        if (colorizedImageUri != null) {
            val file = File(Uri.parse(colorizedImageUri).path!!)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.imageView4.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "No colorized image found", Toast.LENGTH_SHORT).show()
        }

        // FloatingActionButton to navigate to another activity
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        // Wishlist button functionality
        binding.floatingActionButton4.setOnClickListener {
            if (isFavorite) {
                binding.floatingActionButton4.imageTintList = ContextCompat.getColorStateList(this, R.color.black)
                binding.floatingActionButton4.setImageResource(R.drawable.fav)
                Toast.makeText(this, "Removed from favourites.", Toast.LENGTH_SHORT).show()
            } else {
                binding.floatingActionButton4.imageTintList = ContextCompat.getColorStateList(this, R.color.red)
                binding.floatingActionButton4.setImageResource(R.drawable.favo)
                Toast.makeText(this, "Added to favourites.", Toast.LENGTH_SHORT).show()
            }
            isFavorite = !isFavorite
        }

        // Share button functionality
        binding.floatingActionButton3.setOnClickListener {
            val drawable = binding.imageView4.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Colourized Image", null)
                val uri = Uri.parse(path)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, uri)
                }
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            } else {
                Toast.makeText(this, "No image available to share.", Toast.LENGTH_SHORT).show()
            }
        }

        // Save image to device functionality
        binding.floatingActionButton.setOnClickListener {
            val drawable = binding.imageView4.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "colorized_image_${System.currentTimeMillis()}.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let { uri ->
                    try {
                        val outputStream = contentResolver.openOutputStream(uri)
                        // Use safe call to ensure outputStream is not null
                        outputStream?.let { stream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            stream.close()

                            // Notify the media scanner to update the gallery
                            sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))

                            Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(this, "Failed to open output stream", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        Toast.makeText(this, "Error saving image: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } ?: run {
                    Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
