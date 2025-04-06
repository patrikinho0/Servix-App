package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    private var selectedItemId: Int = R.id.services

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.services)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnItemSelectedListener { item ->
            selectedItemId = item.itemId
            val intent = when (item.itemId) {
                R.id.home -> Intent(this, MainActivity::class.java)
                R.id.services -> Intent(this, ServicesActivity::class.java)
                R.id.experts -> Intent(this, ExpertsActivity::class.java)
                R.id.notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.profile -> Intent(this, ProfileActivity::class.java)
                else -> return@setOnItemSelectedListener false
            }
            intent.putExtra("selected_item_id", selectedItemId)
            startActivity(intent)
            return@setOnItemSelectedListener true
        }
        updateNavItemStyle(bottomNavigationView)
    }

    private fun updateNavItemStyle(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size) {
            val item = bottomNavigationView.menu[i]
            val itemView = bottomNavigationView.findViewById<View>(item.itemId)
            val textView = itemView?.findViewById<TextView>(android.R.id.title)

            textView?.let {
                if (item.isChecked) {
                    it.setTextAppearance(this, R.style.BottomNavTextSelected)
                } else {
                    it.setTextAppearance(this, R.style.BottomNavTextUnselected)
                }
            }
        }
    }
}