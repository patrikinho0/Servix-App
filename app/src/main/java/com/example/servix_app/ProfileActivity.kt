package com.example.servix_app

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.servix_app.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    private lateinit var greetingTextView: TextView
    private lateinit var profileImageView: ImageView
    private var selectedItemId: Int = R.id.services

    private lateinit var accountInfoButton: AppCompatButton
    private lateinit var appSettingsButton: AppCompatButton
    private lateinit var notificationsButton: AppCompatButton
    private lateinit var likedServicesButton: AppCompatButton
    private lateinit var becomeExpertButton: AppCompatButton
    private lateinit var contactButton: AppCompatButton
    private lateinit var faqButton: AppCompatButton
    private lateinit var regulationsButton: AppCompatButton
    private lateinit var privacyPolicyButton: AppCompatButton
    private lateinit var cookieFilesSettingsButton: AppCompatButton
    private lateinit var rateServixButton: AppCompatButton
    private lateinit var aboutAppButton: AppCompatButton
    private lateinit var licensesButton: AppCompatButton
    private lateinit var logoutButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        greetingTextView = findViewById(R.id.profile_greetings_textView)
        profileImageView = findViewById(R.id.profile_picture_imageView)

        accountInfoButton = findViewById(R.id.account_info_button)
        appSettingsButton = findViewById(R.id.application_settings_button)
        notificationsButton = findViewById(R.id.notifications_button)
        likedServicesButton = findViewById(R.id.liked_services_button)
        becomeExpertButton = findViewById(R.id.become_expert_button)
        contactButton = findViewById(R.id.contact_button)
        faqButton = findViewById(R.id.faq_button)
        regulationsButton = findViewById(R.id.regulations_button)
        privacyPolicyButton = findViewById(R.id.privacy_policy_button)
        cookieFilesSettingsButton = findViewById(R.id.cookie_files_settings_button)
        rateServixButton = findViewById(R.id.rate_servix_button)
        aboutAppButton = findViewById(R.id.about_app_button)
        licensesButton = findViewById(R.id.licenses_button)
        logoutButton = findViewById(R.id.logout_button)

        selectedItemId = intent.getIntExtra("selected_item_id", R.id.services)

        setupCustomBottomNav()
        loadUserInfo()
        setupButtonListeners()
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
            else -> {}
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
        setTextColor(ContextCompat.getColor(this@ProfileActivity, R.color.black))
        textSize = 14f
    }

    private fun loadUserInfo() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(user.uid)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name") ?: "Unknown"
                    val profileImageUrl = document.getString("profilePictureUrl")

                    greetingTextView.text = "Hey, $name"

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
        }

        accountInfoButton.setOnClickListener {
            startActivity(Intent(this, AccountInfoActivity::class.java)
                .putExtra("selected_item_id", -1))
        }

        appSettingsButton.setOnClickListener {
            startActivity(Intent(this, AppSettingsActivity::class.java)
                .putExtra("selected_item_id", -1))
        }

        notificationsButton.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java)
                .putExtra("selected_item_id", R.id.notifications))
        }

        likedServicesButton.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java)
                .putExtra("selected_item_id", R.id.services))
        }

        becomeExpertButton.setOnClickListener {
            startActivity(Intent(this, ExpertsActivity::class.java)
                .putExtra("selected_item_id", R.id.experts))
        }

        contactButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:support@servix.com".toUri()
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(emailIntent)
        }

        faqButton.setOnClickListener {
            val formUrl = "https://forms.gle/tfVmoPb6D8wwm3LT7"
            val intent = Intent(Intent.ACTION_VIEW, formUrl.toUri())
            startActivity(intent)
        }

        privacyPolicyButton.setOnClickListener {
            val docUrl = "https://docs.google.com/document/d/1jwtkceDzdHQJTOFAwKMj8_CDtpFyJxD4oGuyiQQFSbE/edit?usp=sharing"
            val intent = Intent(Intent.ACTION_VIEW, docUrl.toUri())
            startActivity(intent)
        }

        regulationsButton.setOnClickListener {
            val docUrl = "https://docs.google.com/document/d/1jwtkceDzdHQJTOFAwKMj8_CDtpFyJxD4oGuyiQQFSbE/edit?usp=sharing"
            val intent = Intent(Intent.ACTION_VIEW, docUrl.toUri())
            startActivity(intent)
        }

        cookieFilesSettingsButton.setOnClickListener {
            val options = arrayOf("Yeah, no problem", "No, please don't")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Should we collect your cookies?")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> handleCookieConsent(true)
                    1 -> handleCookieConsent(false)
                }
            }
            builder.setCancelable(true)
            builder.show()
        }

        rateServixButton.setOnClickListener {
            val appPackageName = packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$appPackageName".toUri()))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$appPackageName".toUri()))
            }
        }

        aboutAppButton.setOnClickListener {
            val docUrl = "https://docs.google.com/document/d/18KASSuFusblEackAFyhiDn2C8uSbVv2qqIqM98S3aJ4/edit?usp=sharing"
            val intent = Intent(Intent.ACTION_VIEW, docUrl.toUri())
            startActivity(intent)
        }

        licensesButton.setOnClickListener {
            val docUrl = "https://docs.google.com/document/d/18KASSuFusblEackAFyhiDn2C8uSbVv2qqIqM98S3aJ4/edit?usp=sharing"
            val intent = Intent(Intent.ACTION_VIEW, docUrl.toUri())
            startActivity(intent)
        }
    }

    private fun handleCookieConsent(consentGiven: Boolean) {
        if (consentGiven) {
            Toast.makeText(this, "You agreed to cookie collection", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "You declined cookie collection", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java)
            .putExtra("selected_item_id", R.id.home))
        super.onBackPressed()
    }
}
