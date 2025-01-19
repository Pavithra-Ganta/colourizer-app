package com.example.colourizer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView

object MenuHandler {

    private lateinit var googleSignInClient: GoogleSignInClient

    /**
     * Sets up the app bar with navigation functionality.
     */
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

    /**
     * Sets up a navigation drawer with the given toolbar and drawer layout.
     */
    fun setupDrawer(
        activity: Activity,
        toolbar: Toolbar,
        drawerLayout: DrawerLayout,
        navView: NavigationView
    ) {
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        navView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            toolbar,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuSelection(activity, menuItem)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    /**
     * Handles menu item selection for the popup or navigation drawer menu.
     */
    fun handleMenuSelection(context: Context, menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_gal -> {
                context.startActivity(Intent(context, Gallery::class.java))
            }
            R.id.nav_fav -> {
                context.startActivity(Intent(context, Favourites::class.java))
            }
            R.id.nav_profile -> {
                Toast.makeText(context, "Profile selected", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_logout -> {
                // Ensure Google Sign-In Client is initialized
                if (!::googleSignInClient.isInitialized) {
                    Toast.makeText(context, "GoogleSignInClient not initialized", Toast.LENGTH_SHORT).show()
                    return false
                }

                // Handle Google Sign-Out
                googleSignInClient.signOut().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        context.startActivity(intent)

                        // Check if context is an Activity and finish it
                        if (context is Activity) {
                            context.finish()
                        }
                    } else {
                        Toast.makeText(context, "Error signing out. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.nav_abt -> {
                context.startActivity(Intent(context, About::class.java))
            }
            R.id.nav_rate -> {
                Toast.makeText(context, "Rate Us selected", Toast.LENGTH_SHORT).show()
            }
            else -> return false
        }
        return true
    }
}