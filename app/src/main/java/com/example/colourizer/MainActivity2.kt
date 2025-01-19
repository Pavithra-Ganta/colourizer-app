package com.example.colourizer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity2 : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var img: ImageView
    private lateinit var btn: Button
    private lateinit var btn2: Button
    private lateinit var toolbar: Toolbar
    private lateinit var home: ImageButton
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        img = findViewById(R.id.imageView2)
        btn = findViewById(R.id.button2)
        btn.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        btn2 = findViewById(R.id.button)
        btn2.setOnClickListener {
            selectedImageFile?.let {
                uploadImage(it)
            } ?: run {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize views

        toolbar = findViewById(R.id.toolbar)
        home = findViewById(R.id.home)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        // Use MenuHandler to set up the drawer
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupDrawer(this, toolbar, drawerLayout, navView)

        // Set the navigation item selected listener
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = MenuHandler.handleMenuSelection(this, item)
        drawerLayout.closeDrawer(GravityCompat.START)
        return handled
    }
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && resultCode == RESULT_OK) {
            selectedImageUri = data.data
            img.setImageURI(selectedImageUri)
            selectedImageUri?.let {
                selectedImageFile = File(it.path ?: "")
            }
        }
    }

    private fun uploadImage(imageFile: File) {
        val url = "http://192.168.17.135:5000/colorize"
        val client = OkHttpClient()

        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestBody = imageFile.asRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.name, requestBody)
                .build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity2, "Failed to connect to server: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity2", "Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body
                        responseBody?.let { body ->
                            val tempFile = File.createTempFile("colorized_", ".jpg", cacheDir)
                            tempFile.outputStream().use { fileOut ->
                                fileOut.write(body.bytes())
                            }
                            uploadToImgur(tempFile)
                        }
                    } else {
                        val errorMessage = response.message
                        runOnUiThread {
                            Toast.makeText(this@MainActivity2, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("MainActivity2", "Error: $errorMessage")
                    }
                }
            }
        })
    }

    private fun uploadToImgur(imageFile: File) {
        val clientId = "790707c19b27f57"
        val client = OkHttpClient()

        val mediaType = "image/jpeg".toMediaTypeOrNull()
        val requestBody = imageFile.asRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://api.imgur.com/3/upload")
            .post(MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("image", imageFile.name, requestBody)
                .build())
            .addHeader("Authorization", "Client-ID $clientId")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity2, "Failed to upload to Imgur: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity2", "Imgur upload error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(response.body!!.string())
                        val imgurLink = jsonResponse.getJSONObject("data").getString("link")
                        storeLinkInFirebase(imgurLink)
                    } else {
                        val errorMessage = response.message
                        runOnUiThread {
                            Toast.makeText(this@MainActivity2, "Imgur upload failed: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                        Log.e("MainActivity2", "Imgur upload error: $errorMessage")
                    }
                }
            }
        })
    }

    private fun storeLinkInFirebase(imgurLink: String) {
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val database = Firebase.database
            val ref = database.reference.child("users").child(userId).child("colorized_images")
            ref.push().setValue(imgurLink).addOnSuccessListener {
                runOnUiThread {
                    Toast.makeText(this@MainActivity2, "Image link saved to Firebase!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity2, MainActivity3::class.java)
                    intent.putExtra("colorized_image_path", imgurLink)
                    startActivity(intent)
                }
            }.addOnFailureListener { e ->
                runOnUiThread {
                    Toast.makeText(this@MainActivity2, "Failed to save link to Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                Log.e("MainActivity2", "Firebase save error: ${e.message}")
            }
        } else {
            runOnUiThread {
                Toast.makeText(this@MainActivity2, "User not logged in.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}