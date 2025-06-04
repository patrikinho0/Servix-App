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
import java.util.ArrayList // Explicitly import ArrayList

class ServicesActivity : AppCompatActivity(), OnServiceClickListener {

    private var selectedItemId: Int = R.id.services
    private val db = Firebase.firestore

    // RecyclerView and Adapter properties
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private val announcements = mutableListOf<Announcement>() // This will hold your data

    // Define a request code for starting AddPostActivity
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
        myAdapter = MyAdapter(announcements, this) // Pass the mutable list
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initial load of services when the activity is created
        loadServices()

        val serviceButton: Button = findViewById(R.id.services_addService_button)
        serviceButton.setOnClickListener {
            val addServiceIntent = Intent(this, AddPostActivity::class.java)
            // --- START ACTIVITY FOR RESULT ---
            startActivityForResult(addServiceIntent, ADD_SERVICE_REQUEST_CODE)
            // --- END START ACTIVITY FOR RESULT ---
        }

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)
        setupCustomBottomNav()
    }

    // --- NEW METHOD: Encapsulate data fetching logic ---
    private fun loadServices() {
        db.collection("services")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                announcements.clear() // Clear existing data
                for (document in documents) {
                    val announcement = document.toObject(Announcement::class.java)
                    announcement.id = document.id // Ensure ID is set
                    announcements.add(announcement)
                }
                myAdapter.updateData(announcements) // Update adapter with new data
                Log.d("ServicesActivity", "Services data loaded successfully. Count: ${announcements.size}")
            }
            .addOnFailureListener { exception ->
                Log.w("ServicesActivity", "Error getting documents: ", exception)
            }
    }
    // --- END NEW METHOD ---

    // --- OVERRIDE onActivityResult to handle result from AddPostActivity ---
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check if the request code matches and the result was successful (RESULT_OK)
        if (requestCode == ADD_SERVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            // A new service was successfully added, so refresh the list
            Log.d("ServicesActivity", "AddPostActivity returned RESULT_OK, refreshing services.")
            loadServices()
        }
    }
    // --- END OVERRIDE onActivityResult ---

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

    // --- Add onResume() to ensure data refresh if navigating from other activities ---
    override fun onResume() {
        super.onResume()
        // This ensures the services list is always fresh when the activity comes to the foreground.
        // It's a good fallback even if onActivityResult also triggers it.
        loadServices()
    }
    // --- End onResume() ---

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
            // No need to restart ServicesActivity if we are already in ServicesActivity
            // unless there's a specific reason for it in your bottom nav logic.
            // For refreshing, onResume/onActivityResult are sufficient.
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