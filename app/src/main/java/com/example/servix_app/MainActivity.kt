package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private var selectedItemId: Int = R.id.home
    private val db = Firebase.firestore

    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var expertsRecyclerView: RecyclerView
    private lateinit var recommendedRecyclerView: RecyclerView

    private lateinit var serviceAdapter: MyAdapter
    private lateinit var expertAdapter: ExpertAdapter
    private lateinit var recommendedAdapter: MyAdapter

    private lateinit var servicesList: MutableList<Announcement>
    private lateinit var expertsList: MutableList<Expert>
    private lateinit var recommendedList: MutableList<Announcement>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupFilterButton()
        setupCustomBottomNav()

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)

        loadServices(orderBy = "desc")
        loadExperts()
        loadRecommendedServices()
    }

    private fun setupRecyclerView() {
        servicesList = mutableListOf()
        expertsList = mutableListOf()
        recommendedList = mutableListOf()

        serviceAdapter = MyAdapter(servicesList)
        expertAdapter = ExpertAdapter(expertsList)
        recommendedAdapter = MyAdapter(recommendedList)

        servicesRecyclerView = findViewById(R.id.recyclerView_services)
        expertsRecyclerView = findViewById(R.id.recyclerView_experts)
        recommendedRecyclerView = findViewById(R.id.recyclerView_recommended)

        servicesRecyclerView.adapter = serviceAdapter
        expertsRecyclerView.adapter = expertAdapter
        recommendedRecyclerView.adapter = recommendedAdapter

        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
        expertsRecyclerView.layoutManager = LinearLayoutManager(this)
        recommendedRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadRecommendedServices() {
        db.collection("services")
            .get()
            .addOnSuccessListener { documents ->
                val allServices = documents.mapNotNull { it.toObject(Announcement::class.java) }

                val randomServices = allServices.shuffled().take(2)

                recommendedList.clear()
                recommendedList.addAll(randomServices)
                recommendedAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error loading recommended services: ", exception)
            }
    }


    private fun setupFilterButton() {
        val filterIcon = findViewById<ImageView>(R.id.main_filter_imageView)
        filterIcon.setOnClickListener { showFilterDialog() }
    }

    private fun showFilterDialog() {
        val options = arrayOf("Newest First", "Oldest First")
        AlertDialog.Builder(this)
            .setTitle("Sort By")
            .setItems(options) { _, which ->
                val order = if (which == 0) "desc" else "asc"
                loadServices(order)
            }
            .show()
    }

    private fun loadServices(orderBy: String) {
        db.collection("services")
            .orderBy("date", if (orderBy == "desc") Query.Direction.DESCENDING else Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                servicesList.clear()
                for (document in documents) {
                    val announcement = document.toObject(Announcement::class.java)
                    servicesList.add(announcement)
                }
                serviceAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error loading services: ", exception)
            }
    }

    private fun loadExperts() {
        db.collection("experts")
            .get()
            .addOnSuccessListener { documents ->
                expertsList.clear()
                for (document in documents) {
                    val expert = document.toObject(Expert::class.java)
                    expertsList.add(expert)
                }
                expertAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error loading experts: ", exception)
            }
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
        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        textSize = 14f
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
