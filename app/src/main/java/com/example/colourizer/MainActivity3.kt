package com.example.colourizer

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.colourizer.databinding.ActivityMain3Binding
import java.io.File

class MainActivity3 : AppCompatActivity() {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use view binding to inflate the layout
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root) // Set the root view for binding

        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar)

        // Display the colorized image
        val colorizedImageUri = intent.getStringExtra("colorized_image_path")
        if (colorizedImageUri != null) {
            val file = File(Uri.parse(colorizedImageUri).path!!)
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            binding.imageView4.setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "No colorized image found", Toast.LENGTH_SHORT).show()
        }
    }
}
