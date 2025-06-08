package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log // Make sure Log is imported
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
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
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity(), OnServiceClickListener {
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

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)

        Log.d("MainActivity", "Attempting to load services (main)")
        loadServices(orderBy = "desc")
        Log.d("MainActivity", "Attempting to load experts")
        loadExperts()
        Log.d("MainActivity", "Attempting to load recommended services")
        loadRecommendedServices()

        setupFilterButton()
        setupCustomBottomNav()

        val searchView = findViewById<SearchView>(R.id.main_searchView)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMain(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMain(newText)
                return true
            }
        })
    }

    private fun filterMain(query: String?) {
        if (query.isNullOrEmpty()) {
            serviceAdapter.updateData(servicesList)
            expertAdapter.updateData(expertsList)
            return
        }

        val filteredServices = servicesList.filter {
            it.title?.contains(query, ignoreCase = true) == true ||
                    it.location?.contains(query, ignoreCase = true) == true
        }

        val filteredExperts = expertsList.filter {
            it.name.contains(query, ignoreCase = true) == true ||
                    it.expertise.contains(query, ignoreCase = true) == true
        }
        serviceAdapter.updateData(filteredServices.toMutableList())
        expertAdapter.updateData(filteredExperts.toMutableList())
    }

    private fun setupRecyclerView() {
        servicesList = mutableListOf()
        expertsList = mutableListOf()
        recommendedList = mutableListOf()

        servicesRecyclerView = findViewById(R.id.recyclerView_services)
        expertsRecyclerView = findViewById(R.id.recyclerView_experts)
        recommendedRecyclerView = findViewById(R.id.recyclerView_recommended)

        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
        expertsRecyclerView.layoutManager = LinearLayoutManager(this)
        recommendedRecyclerView.layoutManager = LinearLayoutManager(this)

        serviceAdapter = MyAdapter(servicesList, this)
        servicesRecyclerView.adapter = serviceAdapter

        expertAdapter = ExpertAdapter(expertsList)
        expertsRecyclerView.adapter = expertAdapter

        recommendedAdapter = MyAdapter(recommendedList, this)
        recommendedRecyclerView.adapter = recommendedAdapter
    }

    private fun loadRecommendedServices() {
        db.collection("services")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("MainActivity", "loadRecommendedServices: Documents fetched. Size: ${documents.size()}")
                recommendedList.clear()
                for (document in documents) {
                    try {
                        val announcement = document.toObject(Announcement::class.java)
                        announcement.id = document.id
                        recommendedList.add(announcement)
                        Log.d("MainActivity", "loadRecommendedServices: Added ID: ${announcement.id}, Title: ${announcement.title}, Likes: ${announcement.likes}")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "loadRecommendedServices: Error converting document ${document.id} to Announcement: ${e.message}", e)
                    }
                }
                val randomServices = recommendedList.shuffled().take(2)
                recommendedList.clear()
                recommendedList.addAll(randomServices)
                Log.d("MainActivity", "loadRecommendedServices: Displaying ${recommendedList.size} recommended services.")
                recommendedAdapter.updateData(recommendedList)
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error loading recommended services: ", exception)
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
                Log.d("MainActivity", "loadServices: Documents fetched. Size: ${documents.size()}")
                servicesList.clear()
                for (document in documents) {
                    try {
                        val announcement = document.toObject(Announcement::class.java)
                        announcement.id = document.id
                        servicesList.add(announcement)
                        Log.d("MainActivity", "loadServices: Added ID: ${announcement.id}, Title: ${announcement.title}, Likes: ${announcement.likes}")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "loadServices: Error converting document ${document.id} to Announcement: ${e.message}", e)
                    }
                }
                Log.d("MainActivity", "loadServices: Displaying ${servicesList.size} services.")
                serviceAdapter.updateData(servicesList)
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error loading services: ", exception)
            }
    }

    private fun loadExperts() {
        db.collection("experts")
            .get()
            .addOnSuccessListener { documents ->
                Log.d("MainActivity", "loadExperts: Documents fetched. Size: ${documents.size()}")
                expertsList.clear()
                for (document in documents) {
                    try {
                        val expert = document.toObject(Expert::class.java)
                        expertsList.add(expert)
                        Log.d("MainActivity", "loadExperts: Added ID: ${document.id}, Name: ${expert.name}")
                    } catch (e: Exception) {
                        Log.e("MainActivity", "loadExperts: Error converting document ${document.id} to Expert: ${e.message}", e)
                    }
                }
                Log.d("MainActivity", "loadExperts: Displaying ${expertsList.size} experts.")
                expertAdapter.updateData(expertsList)
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error loading experts: ", exception)
            }
    }

    override fun onServiceClick(announcement: Announcement) {
        val intent = Intent(this, SingleServiceActivity::class.java).apply {
            putExtra("serviceId", announcement.id)
            putExtra("title", announcement.title)
            putExtra("description", announcement.description)
            val formattedDate = announcement.date?.toDate()?.let {
                SimpleDateFormat("dd MMM yyyy 'at' hh:mm:ss a", Locale.getDefault()).format(it)
            } ?: ""
            putExtra("date", formattedDate)
            putExtra("location", announcement.location)
            putExtra("uid", announcement.uid)
            putStringArrayListExtra("images", ArrayList(announcement.images.filterNotNull()))
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
        setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
        textSize = 14f
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}