package com.example.colourizer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.github.dhaval2404.imagepicker.ImagePicker

class MainActivity2 : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var btn: Button
    private lateinit var btn2: Button
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton

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
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
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
            img.setImageURI(data.data)
        }
    }
}
