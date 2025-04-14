package com.example.servix_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import androidx.core.view.size

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private lateinit var greetingTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var logoutButton: View
    private var selectedItemId: Int = R.id.services

    private lateinit var accountInfoButton: AppCompatButton
    private lateinit var appSettingsButton: AppCompatButton
    private lateinit var notificationsButton: AppCompatButton
    private lateinit var likedServicesButton: AppCompatButton
    private lateinit var becomeExpertButton: AppCompatButton
    private lateinit var contactButton: AppCompatButton
    private lateinit var faqButton: AppCompatButton
    private lateinit var regulationsButton: AppCompatButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        accountInfoButton = findViewById(R.id.account_info_button)
        appSettingsButton = findViewById(R.id.app_settings_button)
        notificationsButton = findViewById(R.id.notifications_button)
        likedServicesButton = findViewById(R.id.liked_services_button)
        becomeExpertButton = findViewById(R.id.become_expert_button)
        contactButton = findViewById(R.id.contact_button)
        faqButton = findViewById(R.id.faq_button)
        regulationsButton = findViewById(R.id.regulations_button)

        greetingTextView = findViewById(R.id.profile_greetings_textView)
        profileImageView = findViewById(R.id.profile_picture_imageView)
        logoutButton = findViewById(R.id.logout_button)

        setupBottomNavigation()
        loadUserInfo()
        setupButtonListeners()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.services)
        bottomNavigationView.selectedItemId = selectedItemId

        bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId == selectedItemId) return@setOnItemSelectedListener true

            selectedItemId = item.itemId
            val intent = when (item.itemId) {
                R.id.home -> Intent(this, MainActivity::class.java)
                R.id.services -> Intent(this, ServicesActivity::class.java)
                R.id.experts -> Intent(this, ExpertsActivity::class.java)
                R.id.notifications -> Intent(this, NotificationsActivity::class.java)
                R.id.profile -> return@setOnItemSelectedListener true
                else -> return@setOnItemSelectedListener false
            }
            intent.putExtra("selected_item_id", selectedItemId)
            startActivity(intent)
            true
        }

        updateNavItemStyle(bottomNavigationView)
    }

    private fun updateNavItemStyle(bottomNavigationView: BottomNavigationView) {
        for (i in 0 until bottomNavigationView.menu.size) {
            val item = bottomNavigationView.menu[i]
            val itemView = bottomNavigationView.findViewById<View>(item.itemId)
            val textView = itemView?.findViewById<TextView>(android.R.id.title)
            textView?.setTextAppearance(
                this,
                if (item.isChecked) R.style.BottomNavTextSelected else R.style.BottomNavTextUnselected
            )
        }
    }

    private fun loadUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = Firebase.firestore
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Unknown"
                    val profileImageUrl = document.getString("profilePictureUrl")

                    val usernameTextView: TextView = findViewById(R.id.profile_greetings_textView)
                    val profileImageView: ImageView = findViewById(R.id.profile_picture_imageView)

                    usernameTextView.text = "Hey, $name"

                    profileImageUrl?.let { url ->
                        Picasso.get().load(url).into(profileImageView)
                    }
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupButtonListeners() {
        logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        accountInfoButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        appSettingsButton.setOnClickListener {
            Toast.makeText(this, "App Settings clicked", Toast.LENGTH_SHORT).show()
        }

        notificationsButton.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }

        likedServicesButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        becomeExpertButton.setOnClickListener {
            Toast.makeText(this, "Become Expert clicked", Toast.LENGTH_SHORT).show()
        }

        contactButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@servix.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(emailIntent)
        }

        faqButton.setOnClickListener {
            Toast.makeText(this, "FAQ clicked", Toast.LENGTH_SHORT).show()
        }

        regulationsButton.setOnClickListener {
            Toast.makeText(this, "Terms & Regulations clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
