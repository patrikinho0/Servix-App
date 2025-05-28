package com.example.servix_app

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.AppCompatButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit
import androidx.core.net.toUri

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
        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)
        setupCustomBottomNav()
    }
    private fun setupCustomBottomNav() {
        val themeSettingsButton = findViewById<AppCompatButton>(R.id.theme_settings_button)
        val navHome = findViewById<View>(R.id.navHome)
        val navServices = findViewById<View>(R.id.navServices)
        val navExperts = findViewById<View>(R.id.navExperts)
        val navNotifications = findViewById<View>(R.id.navNotifications)
        val navProfile = findViewById<View>(R.id.navProfile)

        val homeText = findViewById<TextView>(R.id.navHome_textView)
        val servicesText = findViewById<TextView>(R.id.navServices_textView)
        val expertsText = findViewById<TextView>(R.id.navExperts_textView)
        val notificationsText = findViewById<TextView>(R.id.navNotify_textView)
        val profileText = findViewById<TextView>(R.id.navProfile_textView)

        val allTexts = listOf(homeText, servicesText, expertsText, notificationsText, profileText)

        allTexts.forEach { text ->
            text.setTypeface(null, Typeface.NORMAL)
            text.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        when (selectedItemId) {
            R.id.home -> homeText.setBoldActive()
            R.id.services -> servicesText.setBoldActive()
            R.id.experts -> expertsText.setBoldActive()
            R.id.notifications -> notificationsText.setBoldActive()
            R.id.profile -> profileText.setBoldActive()
        }

        navHome.setOnClickListener {
            if (selectedItemId != R.id.home) {
                startActivity(Intent(this, MainActivity::class.java)
                    .putExtra("selected_item_id", R.id.home))
            }
        }

        navServices.setOnClickListener {
            if (selectedItemId != R.id.services) {
                startActivity(Intent(this, ServicesActivity::class.java)
                    .putExtra("selected_item_id", R.id.services))
            }
        }

        navExperts.setOnClickListener {
            if (selectedItemId != R.id.experts) {
                startActivity(Intent(this, ExpertsActivity::class.java)
                    .putExtra("selected_item_id", R.id.experts))
            }
        }

        navNotifications.setOnClickListener {
            if (selectedItemId != R.id.notifications) {
                startActivity(Intent(this, NotificationsActivity::class.java)
                    .putExtra("selected_item_id", R.id.notifications))
            }
        }

        navProfile.setOnClickListener {
            if (selectedItemId != R.id.profile) {
                startActivity(Intent(this, ProfileActivity::class.java)
                    .putExtra("selected_item_id", R.id.profile))
            }
        }

        themeSettingsButton.setOnClickListener {
            startActivity(Intent(this, ThemeSettingsActivity::class.java))
        }
    }

    private fun TextView.setBoldActive() {
        setTypeface(null, Typeface.BOLD)
        setTextColor(ContextCompat.getColor(this@AppSettingsActivity, R.color.black))
        textSize = 14f
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ProfileActivity::class.java)
            .putExtra("selected_item_id", R.id.profile))
        super.onBackPressed()
    }
}
