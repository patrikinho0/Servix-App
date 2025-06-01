package com.example.servix_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingleServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_service)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        val titleText = findViewById<TextView>(R.id.serviceTitle)
        val descText = findViewById<TextView>(R.id.serviceDescription)
        val dateText = findViewById<TextView>(R.id.serviceDate)
        val locationText = findViewById<TextView>(R.id.serviceLocation)
        val contactButton = findViewById<Button>(R.id.contactButton)
        val authorText = findViewById<TextView>(R.id.serviceAuthor)

        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val uid = intent.getStringExtra("uid") ?: ""
        val imageUrls = intent.getStringArrayListExtra("images") ?: arrayListOf()

        titleText.text = title
        descText.text = description
        dateText.text = "Added: $date"
        locationText.text = location

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = ImageUrlAdapter(imageUrls)

        if (uid.isNotEmpty()) {
            authorText.text = "Author: Loading..."
            Firebase.firestore.collection("users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val authorName = documentSnapshot.getString("name")
                        val authorEmail = documentSnapshot.getString("email")

                        Log.d("SingleServiceActivity", "Fetched Author Email: $authorEmail")

                        authorText.text = "Author: ${authorName ?: "Unknown"}"

                        contactButton.setOnClickListener {
                            if (authorEmail != null && authorEmail.isNotEmpty()) {
                                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain" //
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(authorEmail))
                                    putExtra(Intent.EXTRA_SUBJECT, "Interest in your service: $title")
                                     putExtra(Intent.EXTRA_TEXT, "Hello, I'm interested in your service: $title.")
                                }

                                if (emailIntent.resolveActivity(packageManager) != null) {
                                    startActivity(Intent.createChooser(emailIntent, "Send email using..."))
                                } else {
                                    Toast.makeText(this, "No email app found on this device.", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Author's email not available in Firestore.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        authorText.text = "Author: Unknown (User Not Found)"
                        contactButton.setOnClickListener {
                            Toast.makeText(this, "Author not found, cannot contact.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    authorText.text = "Author: Error"
                    contactButton.setOnClickListener {
                        Toast.makeText(this, "Error fetching author details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            authorText.text = "Author: Unknown (No UID)"
            contactButton.setOnClickListener {
                Toast.makeText(this, "Cannot contact: No author UID available.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}