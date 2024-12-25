package com.example.colourizer

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.AdapterClass
import com.example.recyclerview.RecyclerItem

class Gallery : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        toolbar = findViewById(R.id.toolbar)
        menuButton = findViewById(R.id.menuButton)

        // Set up the app bar and menu button using MenuHandler
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupMenu(this, menuButton)

        recyclerView = findViewById(R.id.recyclerview)

        val dataList = arrayListOf(
            RecyclerItem(R.drawable.pics, "Image 1"),
            RecyclerItem(R.drawable.pics, "Image 2"),
            RecyclerItem(R.drawable.pics, "Image 3"),
            RecyclerItem(R.drawable.pics, "Image 4"),
            RecyclerItem(R.drawable.pics, "Image 5"),
            RecyclerItem(R.drawable.pics, "Image 6"),
            RecyclerItem(R.drawable.pics, "Image 7"),
            RecyclerItem(R.drawable.pics, "Image 8"),
            RecyclerItem(R.drawable.pics, "Image 9"),
            RecyclerItem(R.drawable.pics, "Image 10")
        )

        val adapter = AdapterClass(dataList)
        recyclerView.adapter = adapter
        // Use GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)


    }
}
