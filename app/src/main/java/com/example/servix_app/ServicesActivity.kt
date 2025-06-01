package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri // Import Uri for mailto
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale // Import Locale for SimpleDateFormat

class ServicesActivity : AppCompatActivity(), OnServiceClickListener {
    private var selectedItemId: Int = R.id.services
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_services)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val announcements = mutableListOf<Announcement>()
        val myAdapter = MyAdapter(announcements, this)
        val myRecyclerView: RecyclerView = findViewById(R.id.servicesRecyclerView)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        db.collection("services")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                announcements.clear()
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val location = document.getString("location") ?: ""
                    val description = document.getString("description") ?: ""
                    val uid = document.getString("uid") ?: ""
                    val images = document.get("images") as? List<String> ?: emptyList()
                    val date = document.getTimestamp("date") ?: Timestamp.now()

                    val announcement = Announcement(images, title, location, description, date, uid)
                    announcements.add(announcement)
                }
                myAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("ServicesActivity", "Error getting services: ", exception)
            }


        val serviceButton: Button = findViewById(R.id.services_addService_button)

        serviceButton.setOnClickListener {
            val addServiceIntent = Intent(this, AddPostActivity::class.java)
            startActivity(addServiceIntent)
        }
        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)
        setupCustomBottomNav()
    }

    override fun onServiceClick(announcement: Announcement) {
        val intent = Intent(this, SingleServiceActivity::class.java).apply {
            putExtra("title", announcement.title)
            putExtra("description", announcement.description)
            putExtra("date", SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(announcement.date.toDate()))
            putExtra("location", announcement.location)
            putExtra("uid", announcement.uid)
            putStringArrayListExtra("images", ArrayList(announcement.images))
        }
        startActivity(intent)
    }

    private fun setupCustomBottomNav() {
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
    }

    private fun TextView.setBoldActive() {
        setTypeface(null, Typeface.BOLD)
        setTextColor(ContextCompat.getColor(this@ServicesActivity, R.color.black))
        textSize = 14f
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
            .putExtra("selected_item_id", R.id.home))
        super.onBackPressed()
    }
}