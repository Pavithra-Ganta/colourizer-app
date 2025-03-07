package com.example.colourizer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.AdapterClass
import com.example.recyclerview.ImageData
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.appcompat.app.AlertDialog

class Gallery : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var toolbar2: Toolbar
    private lateinit var home: ImageButton
    private val database = Firebase.database.reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private val dataList = ArrayList<ImageData>()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var adapter: AdapterClass
    private var isSelectionMode = false
    private var allSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        toolbar = findViewById(R.id.toolbar)
        toolbar2 = findViewById(R.id.toolbar2)

        home = findViewById(R.id.home)
        home.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        fetchColorizedImages()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        MenuHandler.setupAppBar(this, toolbar)
        MenuHandler.setupDrawer(this, toolbar, drawerLayout, navView)

        navView.setNavigationItemSelectedListener(this)
        navView.setCheckedItem(R.id.nav_gal)

        toolbar2.findViewById<ImageButton>(R.id.select_all).setOnClickListener {
            adapter.selectAll(true)
            updateToolbarTitle()
        }

        toolbar2.findViewById<ImageButton>(R.id.delete).setOnClickListener {
            deleteFromFirebase()
            adapter.deleteSelectedItems()
            updateToolbarTitle()
        }
        val selectAllButton = toolbar2.findViewById<ImageButton>(R.id.select_all)
        selectAllButton.setOnClickListener {
            if (allSelected) {
                // Deselect all items
                adapter.deselectAll()
                allSelected = false
                selectAllButton.setImageResource(R.drawable.baseline_select_all_24) // Update icon to "Select All"
            } else {
                // Select all items
                adapter.selectAll(true)
                allSelected = true
                selectAllButton.setImageResource(R.drawable.baseline_deselect_24) // Update icon to "Deselect All"
            }
            updateToolbarTitle() // Update the toolbar title to reflect the selection count
        }

        toolbar2.findViewById<ImageButton>(R.id.delete).setOnClickListener {
            // Show confirmation dialog before deleting
            if (dataList.any { it.selected }) { // Check if any item is selected
                AlertDialog.Builder(this)
                    .setTitle("Delete Selected Items")
                    .setMessage("Are you sure you want to delete the selected items?")
                    .setPositiveButton("Yes") { _, _ ->
                        deleteFromFirebase()
                        adapter.deleteSelectedItems()
                        updateToolbarTitle()
                        exitSelectionMode()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(this, "No items selected to delete", Toast.LENGTH_SHORT).show()
            }
        }

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
                        adapter = AdapterClass(dataList, ::onItemLongPress, ::onItemClick)
                        recyclerView.adapter = adapter
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

    private fun onItemLongPress(position: Int) {
        if (!isSelectionMode) {
            enterSelectionMode()
        }
        toggleItemSelection(position)
    }

    private fun onItemClick(position: Int) {
        if (isSelectionMode) {
            toggleItemSelection(position)
        } else {
            // Get the image URL for the clicked item
            val selectedImage = dataList[position]
            val intent = Intent(this, MainActivity3::class.java).apply {
                putExtra("colorized_image_path", selectedImage.imgUrl)
            }
            startActivity(intent)
        }
    }

    private fun enterSelectionMode() {
        isSelectionMode = true
        toolbar.visibility = View.GONE
        toolbar2.visibility = View.VISIBLE
        adapter.setSelectionMode(true)
    }

    override fun onBackPressed() {
        if (isSelectionMode) {
            exitSelectionMode()
        } else {
            super.onBackPressed() // Exit activity if not in selection mode
        }
    }

    private fun exitSelectionMode() {
        isSelectionMode = false
        toolbar.visibility = View.VISIBLE
        toolbar2.visibility = View.GONE
        adapter.setSelectionMode(false)
        adapter.deselectAll() // Clear selection when exiting selection mode
    }

    private fun toggleItemSelection(position: Int) {
        adapter.toggleSelection(position)
        updateToolbarTitle()
    }

    private fun updateToolbarTitle() {
        val selectedCount = dataList.count { it.selected }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = MenuHandler.handleMenuSelection(this, item)
        drawerLayout.closeDrawer(GravityCompat.START)
        return handled
    }

    private fun deleteFromFirebase() {
        val selectedItems = dataList.filter { it.selected }
        selectedItems.forEach { item ->
            database.child("users").child(userId!!).child("colorized_images")
                .orderByValue().equalTo(item.imgUrl)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (child in snapshot.children) {
                            child.ref.removeValue()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Gallery", "Error deleting item: ${error.message}")
                    }
                })
        }
    }
}