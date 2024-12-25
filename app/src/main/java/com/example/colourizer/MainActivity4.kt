package com.example.colourizer

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity4 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main4)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val B1 = findViewById<Button>(R.id.button3)
        B1.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, B1)
            popupMenu.menuInflater.inflate(R.menu.menu_file, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item1 ->
                        Toast.makeText(
                            this@MainActivity4,
                            "You Clicked : " + item.title, Toast.LENGTH_SHORT
                        ).show()
                    R.id.item2 ->
                        Toast.makeText(
                            this@MainActivity4,
                            "You Clicked : " + item.title, Toast.LENGTH_SHORT
                        ).show()
                    R.id.item3 ->
                        Toast.makeText(
                            this@MainActivity4, "You Clicked : " +
                                    item.title, Toast.LENGTH_SHORT
                        ).show()
                }
                true
            })
            popupMenu.show()
        }
    }
}