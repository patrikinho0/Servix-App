package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private var selectedItemId: Int = R.id.services

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val announcements = mutableListOf(
            Announcement("https://firebasestorage.googleapis.com/v0/b/crud-90e1f.appspot.com/o/service_images%2F499ab01e-eea4-43e7-a1ff-6053a1cab58a?alt=media&token=3eca8883-99a9-4c16-8cfe-968a477a8dcb", "Need help!", "Active", "Zgierz", "07-04-2025")
        )

        val myAdapter = MyAdapter(announcements)
        val myRecyclerView: RecyclerView = findViewById(R.id.recyclerView)
        myRecyclerView.adapter = myAdapter

        myRecyclerView.layoutManager = LinearLayoutManager(this)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)

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