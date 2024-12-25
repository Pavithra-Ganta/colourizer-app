package com.example.colourizer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
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

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use view binding to inflate the layout
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root) // Set the root view for binding

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

        // FloatingActionButton to navigate to another activity
        binding.floatingActionButton2.setOnClickListener {
            val intent = Intent(this, MainActivity4::class.java)
            startActivity(intent)
        }

        // Wishlist button functionality
        var isFavorite = false
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

        // Save image functionality
        binding.floatingActionButton.setOnClickListener {
            val drawable = binding.imageView4.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                val sdCard = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                if (sdCard != null) {
                    val directory = File(sdCard.absolutePath)
                    if (!directory.exists()) {
                        directory.mkdir()
                    }
                    val fileName = "${System.currentTimeMillis()}.jpg"
                    val outputFile = File(directory, fileName)
                    try {
                        val fileOutputStream = FileOutputStream(outputFile)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                        fileOutputStream.flush()
                        fileOutputStream.close()

                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        intent.data = Uri.fromFile(outputFile)
                        sendBroadcast(intent)

                        Toast.makeText(this, "Image Saved Successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: FileNotFoundException) {
                        Toast.makeText(this, "File not found: ${e.message}", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Toast.makeText(this, "Error saving file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Unable to access storage", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
