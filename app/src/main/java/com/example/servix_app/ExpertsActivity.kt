package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ExpertsActivity : AppCompatActivity() {
    private var selectedItemId: Int = R.id.services
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_experts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        selectedItemId = intent.getIntExtra("selected_item_id", R.id.home)
        setupCustomBottomNav()

        val becomeExpertBtn = findViewById<Button>(R.id.experts_becomeExpert_button)

        becomeExpertBtn.setOnClickListener {
            startActivity(Intent(this, BecomeExpertActivity::class.java))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.expertsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val experts = mutableListOf<Expert>()
        val adapter = ExpertAdapter(experts)
        recyclerView.adapter = adapter

        db.collection("experts")
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val expert = doc.toObject(Expert::class.java)
                    experts.add(expert)
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load experts", Toast.LENGTH_SHORT).show()
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
        setTextColor(ContextCompat.getColor(this@ExpertsActivity, R.color.black))
        textSize = 14f
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
            .putExtra("selected_item_id", R.id.home))
        super.onBackPressed()
    }
}