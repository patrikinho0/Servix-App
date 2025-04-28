package com.example.servix_app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.AppCompatButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppSettingsActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var notificationsSwitch: SwitchCompat
    private lateinit var contactSupportButton: AppCompatButton
    private lateinit var logoutButton: AppCompatButton
    private lateinit var versionText: TextView
    private var selectedItemId: Int = R.id.services

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "AppSettingsPrefs"
    private val NOTIFICATIONS_KEY = "notifications_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)

        notificationsSwitch = findViewById(R.id.notifications_switch)
        contactSupportButton = findViewById(R.id.contact_support_button)
        logoutButton = findViewById(R.id.logout_button)
        versionText = findViewById(R.id.version_text)

        versionText.text = "Version 1.0.0"

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val notificationsEnabled = sharedPreferences.getBoolean(NOTIFICATIONS_KEY, true)
        notificationsSwitch.isChecked = notificationsEnabled

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit() {
                putBoolean(NOTIFICATIONS_KEY, isChecked)
            }

            if (isChecked) {
                Toast.makeText(this, "Notifications enabled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notifications disabled", Toast.LENGTH_SHORT).show()
            }
        }

        contactSupportButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:support@servix.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(emailIntent)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setupBottomNavigation()
    }
    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.services)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == selectedItemId) return@setOnItemSelectedListener true

            selectedItemId = item.itemId
            val intent = when (item.itemId) {
                R.id.home -> Intent(this, MainActivity::class.java)
                R.id.services -> Intent(this, ServicesActivity::class.java)
                R.id.experts -> Intent(this, ExpertsActivity::class.java)
                R.id.notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.profile -> return@setOnItemSelectedListener true
                else -> return@setOnItemSelectedListener false
            }
            intent.putExtra("selected_item_id", selectedItemId)
            startActivity(intent)
            true
        }

        updateNavItemStyle(bottomNavigationView)
    }

    private fun updateNavItemStyle(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size) {
            val item = bottomNavigationView.menu[i]
            val itemView = bottomNavigationView.findViewById<View>(item.itemId)
            val textView = itemView?.findViewById<TextView>(android.R.id.title)
            textView?.setTextAppearance(
                this,
                if (item.isChecked) R.style.BottomNavTextSelected else R.style.BottomNavTextUnselected
            )
        }
    }
}
