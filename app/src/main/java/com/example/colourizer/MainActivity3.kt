package com.example.colourizer

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.colourizer.databinding.ActivityMain3Binding
import java.io.IOException
import com.bumptech.glide.Glide
import com.example.recyclerview.ImageData
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity3 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMain3Binding
    private lateinit var toolbar: Toolbar
    private lateinit var home: ImageButton
    private var isFavorite = false
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val dataList = ArrayList<ImageData>()
    private val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (if needed)
        enableEdgeToEdge()

        // Initialize view binding
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract the image path from intent
        val imagePath = intent.getStringExtra("colorized_image_path")
        val imgurLink = intent.getStringExtra("colorized_image_path")

        Glide.with(this)
            .load(imagePath)
            .into(binding.imageView4)


        // Apply system bar insets for proper layout
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize toolbar and menu button
        toolbar = findViewById(R.id.toolbar)
        home = findViewById(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        // Floating action button to navigate to another activity

        // Favourites button functionality
        binding.floatingActionButton4.setOnClickListener {
            if (isFavorite) {
                binding.floatingActionButton4.imageTintList = ContextCompat.getColorStateList(this, R.color.black)
                binding.floatingActionButton4.setImageResource(R.drawable.fav)
                Toast.makeText(this, "Removed from favourites.", Toast.LENGTH_SHORT).show()
                //Delete from database
                deleteFromFirebase()
            } else {
                binding.floatingActionButton4.imageTintList = ContextCompat.getColorStateList(this, R.color.red)
                binding.floatingActionButton4.setImageResource(R.drawable.favo)
                Toast.makeText(this, "Added to favourites.", Toast.LENGTH_SHORT).show()
                val auth = FirebaseAuth.getInstance()
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val database = Firebase.database
                    val ref = database.reference.child("users").child(userId).child("favourite_images")
                    ref.push().setValue(imgurLink).addOnSuccessListener {
                        runOnUiThread {
                            Toast.makeText(this@MainActivity3, "Favourites saved", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e ->
                        runOnUiThread {
                            Toast.makeText(this@MainActivity3, "Failed to save Favourite: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("MainActivity2", "Firebase save error: ${e.message}")
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity3, "User not logged in.", Toast.LENGTH_SHORT).show()
                    }
                }
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
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        // Use MenuHandler to set up the drawer
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupDrawer(this, toolbar, drawerLayout, navView)

        // Set the navigation item selected listener
        navView.setNavigationItemSelectedListener(this)
    }

    private fun deleteFromFirebase() {
        val selectedItems = dataList.filter { it.selected }
        selectedItems.forEach { item ->
            database.child("users").child(userId!!).child("favourite_images")
                .orderByValue().equalTo(item.imgUrl)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            child.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Favourites", "Error deleting item: ${error.message}")
                    }
                })
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = MenuHandler.handleMenuSelection(this, item)
        drawerLayout.closeDrawer(GravityCompat.START)
        return handled
    }
}
