package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button // Import Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth
import com.google.firebase.firestore.ktx.firestore // Import Firebase Firestore
import com.google.firebase.ktx.Firebase // Import Firebase KTX
import com.squareup.picasso.Picasso // Import Picasso

class SingleExpertActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private lateinit var expertProfilePicture: ImageView
    private lateinit var expertName: TextView
    private lateinit var expertExpertise: TextView
    private lateinit var expertDescription: TextView
    private lateinit var expertDate: TextView
    private lateinit var expertRating: TextView
    private lateinit var expertNumberOfRatings: TextView
    private lateinit var backButton: ImageView
    private lateinit var contactExpertButton: Button

    private var expertEmail: String? = null
    private var expertTitleName: String = "Expert"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_expert)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        expertProfilePicture = findViewById(R.id.expertProfilePicture)
        expertName = findViewById(R.id.expertName)
        expertExpertise = findViewById(R.id.expertExpertise)
        expertDescription = findViewById(R.id.expertDescription)
        expertDate = findViewById(R.id.expertDate)
        expertRating = findViewById(R.id.expertRating)
        expertNumberOfRatings = findViewById(R.id.expertNumberOfRatings)
        backButton = findViewById(R.id.backButton)
        contactExpertButton = findViewById(R.id.contactExpertButton)

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val name = intent.getStringExtra("name") ?: "Unknown Expert"
        val expertise = intent.getStringExtra("expertise") ?: "No expertise listed"
        val description = intent.getStringExtra("description") ?: "No description available."
        val profilePictureUrl = intent.getStringExtra("profilePictureUrl")
        val date = intent.getStringExtra("date") ?: "N/A"
        val rating = intent.getStringExtra("rating") ?: "0.0"
        val numberOfRatings = intent.getStringExtra("numberOfRatings") ?: "0"
        val expertUid = intent.getStringExtra("uid")

        expertTitleName = name

        expertName.text = name
        expertExpertise.text = "Expertise: $expertise"
        expertDescription.text = description
        expertDate.text = "Joined: $date"
        expertRating.text = rating
        expertNumberOfRatings.text = "($numberOfRatings ratings)"

        if (!profilePictureUrl.isNullOrEmpty()) {
            Picasso.get()
                .load(profilePictureUrl)
                .placeholder(R.drawable.person)
                .error(R.drawable.person)
                .into(expertProfilePicture)
        } else {
            expertProfilePicture.setImageResource(R.drawable.person)
        }

        if (!expertUid.isNullOrEmpty()) {
            fetchExpertEmail(expertUid)
        } else {
            Toast.makeText(this, "Expert ID not available, cannot contact.", Toast.LENGTH_SHORT).show()
            contactExpertButton.isEnabled = false
        }

        contactExpertButton.setOnClickListener {
            if (!expertEmail.isNullOrEmpty()) {
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(expertEmail))
                    putExtra(Intent.EXTRA_SUBJECT, "Inquiry about your profile: $expertTitleName")
                }
                if (emailIntent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."))
                } else {
                    Toast.makeText(this, "No email app found on your device.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Expert email not available yet.", Toast.LENGTH_SHORT).show()
            }
        }

        Log.d("SingleExpertActivity", "Expert Data: Name=$name, Expertise=$expertise, Rating=$rating, UID=$expertUid")
    }

    private fun fetchExpertEmail(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    expertEmail = documentSnapshot.getString("email")
                    if (expertEmail.isNullOrEmpty()) {
                        Toast.makeText(this, "Expert email not found in database.", Toast.LENGTH_SHORT).show()
                        contactExpertButton.isEnabled = false
                    }
                    Log.d("SingleExpertActivity", "Fetched Expert Email: $expertEmail")
                } else {
                    Toast.makeText(this, "Expert user data not found.", Toast.LENGTH_SHORT).show()
                    contactExpertButton.isEnabled = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("SingleExpertActivity", "Error fetching expert email: ${e.message}", e)
                Toast.makeText(this, "Failed to load expert email.", Toast.LENGTH_SHORT).show()
                contactExpertButton.isEnabled = false
            }
    }
}