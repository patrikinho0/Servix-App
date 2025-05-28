package com.example.servix_app

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThemeSettingsActivity : AppCompatActivity() {

    private lateinit var lightButton: Button
    private lateinit var darkButton: Button
    private lateinit var autoButton: Button

    private lateinit var lightCheck: TextView
    private lateinit var darkCheck: TextView
    private lateinit var autoCheck: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "AppSettingsPrefs"
    private val THEME_MODE_KEY = "theme_mode"

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        applySavedTheme()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_theme_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lightButton = findViewById(R.id.light_mode_button)
        darkButton = findViewById(R.id.dark_mode_button)
        autoButton = findViewById(R.id.auto_mode_button)

        lightCheck = findViewById(R.id.light_check)
        darkCheck = findViewById(R.id.dark_check)
        autoCheck = findViewById(R.id.auto_check)

        updateCheckmarks(getSavedTheme())

        lightButton.setOnClickListener {
            saveTheme(AppCompatDelegate.MODE_NIGHT_NO)
            updateCheckmarks(AppCompatDelegate.MODE_NIGHT_NO)
        }

        darkButton.setOnClickListener {
            saveTheme(AppCompatDelegate.MODE_NIGHT_YES)
            updateCheckmarks(AppCompatDelegate.MODE_NIGHT_YES)
        }

        autoButton.setOnClickListener {
            saveTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            updateCheckmarks(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun getSavedTheme(): Int {
        return sharedPreferences.getInt(THEME_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun saveTheme(mode: Int) {
        sharedPreferences.edit().putInt(THEME_MODE_KEY, mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
        Toast.makeText(this, "Theme updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateCheckmarks(selectedMode: Int) {
        lightCheck.visibility = if (selectedMode == AppCompatDelegate.MODE_NIGHT_NO) View.VISIBLE else View.GONE
        darkCheck.visibility = if (selectedMode == AppCompatDelegate.MODE_NIGHT_YES) View.VISIBLE else View.GONE
        autoCheck.visibility = if (selectedMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) View.VISIBLE else View.GONE
    }

    private fun applySavedTheme() {
        AppCompatDelegate.setDefaultNightMode(getSavedTheme())
    }
}