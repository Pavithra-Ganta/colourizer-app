package com.example.colourizer

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.startActivity

object MenuHandler {

    fun setupAppBar(activity: Activity, toolbar: Toolbar) {
        // Set the toolbar as the support action bar
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(toolbar)
        }

        // Set up the Home button (navigation icon)
        toolbar.setNavigationIcon(R.drawable.baseline_home_24)
        toolbar.setNavigationOnClickListener {
            // Handle Home button click (navigate to the home activity)
            val intent = Intent(activity, MainActivity2::class.java) // Change to your home activity
            activity.startActivity(intent)
        }
    }

    fun setupMenu(activity: Activity, menuButton: ImageButton) {
        menuButton.setOnClickListener {
            val popupMenu = PopupMenu(activity, menuButton)
            popupMenu.menuInflater.inflate(R.menu.menu_file, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                handleMenuItemClick(activity, menuItem)
            }
            popupMenu.show()
        }
    }

    private fun handleMenuItemClick(activity: Activity, item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item1 -> {
                val intent = Intent(activity, Gallery::class.java)
                activity.startActivity(intent)
                true
            }
            R.id.item2 -> {
                val intent = Intent(activity, Gallery::class.java)
                activity.startActivity(intent)
                true
            }
            R.id.item3 -> {
                val intent = Intent(activity, Gallery::class.java)
                activity.startActivity(intent)
                true
            }
            else -> false
        }
    }
}
