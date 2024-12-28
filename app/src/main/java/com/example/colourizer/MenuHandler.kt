package com.example.colourizer

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

object MenuHandler {

    private lateinit var googleSignInClient: GoogleSignInClient

    fun setupAppBar(activity: Activity, toolbar: Toolbar) {
        if (activity is AppCompatActivity) {
            activity.setSupportActionBar(toolbar)
        }

        toolbar.setNavigationIcon(R.drawable.baseline_home_24)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(activity, MainActivity2::class.java) // Change to your home activity
            activity.startActivity(intent)
        }
    }

    fun setupMenu(activity: Activity, menuButton: ImageButton) {
        // Initialize Google Sign-In Client if not already initialized
        if (!::googleSignInClient.isInitialized) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            googleSignInClient = GoogleSignIn.getClient(activity, gso)
        }

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
                // Handle Google Sign-Out
                googleSignInClient.signOut().addOnCompleteListener {
                    Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    activity.finish()
                }
                true
            }
            else -> false
        }
    }
}
