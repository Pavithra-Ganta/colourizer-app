package com.example.colourizer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException

class MainActivity2 : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var btn: Button
    private lateinit var btn2: Button
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton
    private var selectedImageUri: Uri? = null
    private var selectedImageFile: File? = null

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

        // Initialize toolbar and menu button
        toolbar = findViewById(R.id.toolbar)
        menuButton = findViewById(R.id.menuButton)

        // Set up the app bar and menu
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupMenu(this, menuButton)
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
        val url = "http://192.168.158.135:5000/colorize"
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

                            val intent = Intent(this@MainActivity2, MainActivity3::class.java)
                            intent.putExtra("colorized_image_path", tempFile.absolutePath)
                            startActivity(intent)
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
}
