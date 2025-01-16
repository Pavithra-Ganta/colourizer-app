package com.example.colourizer

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.AdapterClass
import com.example.recyclerview.ImageData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Gallery : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var menuButton: ImageButton
    private val database = Firebase.database.reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val dataList = ArrayList<ImageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        toolbar = findViewById(R.id.toolbar)
        menuButton = findViewById(R.id.menuButton)

        // Set up the app bar and menu button using MenuHandler (if applicable)
        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupMenu(this, menuButton)

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchColorizedImages()
    }

    private fun fetchColorizedImages() {
        if (userId != null) {
            database.child("users").child(userId).child("colorized_images")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        dataList.clear()
                        for (child in snapshot.children) {
                            val imgUrl = child.getValue(String::class.java)
                            imgUrl?.let {
                                dataList.add(ImageData(it, "Image ${dataList.size + 1}"))
                            }
                        }
                        recyclerView.adapter = AdapterClass(dataList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Gallery, "Failed to fetch data: ${error.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Gallery", "Database Error: ${error.message}")
                    }
                })
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
