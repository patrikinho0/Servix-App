package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri // Import Uri for mailto
import android.os.Bundle
import android.util.Log // Make sure Log is imported
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
import java.util.ArrayList // Explicitly import ArrayList

class ServicesActivity : AppCompatActivity(), OnServiceClickListener {

    private var selectedItemId: Int = R.id.services
    private val db = Firebase.firestore

    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private val announcements = mutableListOf<Announcement>()

    private val ADD_SERVICE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_services)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        myRecyclerView = findViewById(R.id.servicesRecyclerView)
        myAdapter = MyAdapter(announcements, this)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        Log.d("ServicesActivity", "Attempting to load services.")
        loadServices()

        val serviceButton: Button = findViewById(R.id.services_addService_button)
        serviceButton.setOnClickListener {
            val addServiceIntent = Intent(this, AddPostActivity::class.java)
            startActivityForResult(addServiceIntent, ADD_SERVICE_REQUEST_CODE)
        }

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)
        setupCustomBottomNav()
    }

    private fun loadServices() {
        db.collection("services")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("ServicesActivity", "Firestore query successful. Documents fetched: ${documents.size()}")
                announcements.clear()
                for (document in documents) {
                    try {
                        val announcement = document.toObject(Announcement::class.java)
                        announcement.id = document.id
                        announcements.add(announcement)
                        Log.d("ServicesActivity", "  - Converted document ${document.id}: Title='${announcement.title}', Likes=${announcement.likes}")
                    } catch (e: Exception) {
                        Log.e("ServicesActivity", "Error converting document ${document.id} to Announcement: ${e.message}", e)
                    }
                }
                myAdapter.updateData(announcements)
                Log.d("ServicesActivity", "Adapter updated with ${announcements.size} services.")
            }
            .addOnFailureListener { exception ->
                Log.e("ServicesActivity", "Error getting documents from Firestore: ", exception) // Changed to E for Error
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_SERVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("ServicesActivity", "AddPostActivity returned RESULT_OK, refreshing services.")
            loadServices()
        }
    }

    override fun onServiceClick(announcement: Announcement) {
        val intent = Intent(this, SingleServiceActivity::class.java).apply {
            putExtra("serviceId", announcement.id)
            putExtra("title", announcement.title)
            putExtra("description", announcement.description)
            putExtra("date", announcement.date?.toDate()?.let {
                SimpleDateFormat("dd MMM yyyy 'at' hh:mm:ss a", Locale.getDefault()).format(it)
            })
            putExtra("location", announcement.location)
            putExtra("uid", announcement.uid)
            putStringArrayListExtra("images", ArrayList(announcement.images.filterNotNull()))
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        Log.d("ServicesActivity", "onResume: Refreshing services data.")
        loadServices() // Ensure data is refreshed when returning to the activity
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
            R.id.home -> servicesText.setBoldActive() // This was homeText, should be servicesText for ServicesActivity if it's the current selected item
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