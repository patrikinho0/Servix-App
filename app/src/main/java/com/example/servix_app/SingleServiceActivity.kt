package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SingleServiceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var currentUserId: String? = null
    private var serviceId: String? = null
    private var isBookmarked: Boolean = false

    private lateinit var imageIndicatorContainer: LinearLayout
    private lateinit var imageUrls: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_single_service)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        currentUserId = auth.currentUser?.uid
        Log.d("SingleServiceActivity", "Current User ID on onCreate: $currentUserId")

        val recyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        val bookmarkIcon = findViewById<ImageView>(R.id.bookmarkIcon)
        val titleText = findViewById<TextView>(R.id.serviceTitle)
        val descText = findViewById<TextView>(R.id.serviceDescription)
        val dateText = findViewById<TextView>(R.id.serviceDate)
        val locationText = findViewById<TextView>(R.id.serviceLocation)
        val contactButton = findViewById<Button>(R.id.contactButton)
        val authorText = findViewById<TextView>(R.id.serviceAuthor)
        imageIndicatorContainer = findViewById(R.id.imageIndicatorContainer)

        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val date = intent.getStringExtra("date") ?: ""
        val location = intent.getStringExtra("location") ?: ""
        val uid = intent.getStringExtra("uid") ?: ""
        imageUrls = intent.getStringArrayListExtra("images") ?: arrayListOf()

        serviceId = intent.getStringExtra("serviceId")
        Log.d("SingleServiceActivity", "Service ID from Intent: $serviceId")

        titleText.text = title
        descText.text = description
        dateText.text = "Added: $date"
        locationText.text = location

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = ImageUrlAdapter(imageUrls)

        setupImageIndicator(imageUrls.size)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                    selectDot(firstVisibleItemPosition)
                }
            }
        })

        if (uid.isNotEmpty()) {
            authorText.text = "Author: Loading..."
            db.collection("users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val authorName = documentSnapshot.getString("name")
                        val authorEmail = documentSnapshot.getString("email")

                        Log.d("SingleServiceActivity", "Fetched Author Email: $authorEmail")

                        authorText.text = "Author: ${authorName ?: "Unknown"}"

                        contactButton.setOnClickListener {
                            if (authorEmail != null && authorEmail.isNotEmpty()) {
                                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "message/rfc822"
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(authorEmail))
                                    putExtra(Intent.EXTRA_SUBJECT, "Interest in your service: $title")
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

        if (currentUserId != null && serviceId != null) {
            checkBookmarkStatus(bookmarkIcon)
            bookmarkIcon.setOnClickListener {
                if (isBookmarked) {
                    showUnbookmarkConfirmationDialog(bookmarkIcon)
                } else {
                    showBookmarkConfirmationDialog(bookmarkIcon)
                }
            }
        } else {
            bookmarkIcon.visibility = ImageView.GONE // Hide or disable icon
            Toast.makeText(this, "Login to save services.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupImageIndicator(count: Int) {
        imageIndicatorContainer.removeAllViews()
        if (count <= 1) {
            imageIndicatorContainer.visibility = View.GONE
            return
        } else {
            imageIndicatorContainer.visibility = View.VISIBLE
        }

        val dotSize = resources.getDimensionPixelSize(R.dimen.indicator_dot_size)
        val dotMargin = resources.getDimensionPixelSize(R.dimen.indicator_dot_margin)

        for (i in 0 until count) {
            val dot = ImageView(this)
            val params = LinearLayout.LayoutParams(dotSize, dotSize)
            params.setMargins(dotMargin, 0, dotMargin, 0)
            dot.layoutParams = params
            dot.setImageResource(R.drawable.dot_inactive)
            imageIndicatorContainer.addView(dot)
        }
        selectDot(0)
    }

    private fun selectDot(position: Int) {
        if (imageUrls.isEmpty()) return

        for (i in 0 until imageIndicatorContainer.childCount) {
            val dot = imageIndicatorContainer.getChildAt(i) as ImageView
            if (i == position) {
                dot.setImageResource(R.drawable.dot_active)
            } else {
                dot.setImageResource(R.drawable.dot_inactive)
            }
        }
    }
    private fun checkBookmarkStatus(bookmarkIcon: ImageView) {
        currentUserId?.let { userId ->
            serviceId?.let { sId ->
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val likedServices = documentSnapshot.get("likedServices") as? List<String>
                            isBookmarked = likedServices?.contains(sId) == true
                            updateBookmarkIcon(bookmarkIcon, isBookmarked)
                        } else {
                            isBookmarked = false
                            updateBookmarkIcon(bookmarkIcon, false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SingleServiceActivity", "Error checking bookmark status: ${e.message}", e)
                        isBookmarked = false
                        updateBookmarkIcon(bookmarkIcon, false)
                        Toast.makeText(this, "Error loading bookmark status.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateBookmarkIcon(bookmarkIcon: ImageView, bookmarked: Boolean) {
        if (bookmarked) {
            bookmarkIcon.setImageResource(R.drawable.bookmark_ic_filled)
        } else {
            bookmarkIcon.setImageResource(R.drawable.bookmark_ic)
        }
    }

    private fun showBookmarkConfirmationDialog(bookmarkIcon: ImageView) {
        AlertDialog.Builder(this)
            .setTitle("Save Service?")
            .setMessage("Do you want to save this service to your liked services?")
            .setPositiveButton("Yes") { dialog, _ ->
                updateLikedServices(true, bookmarkIcon)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showUnbookmarkConfirmationDialog(bookmarkIcon: ImageView) {
        AlertDialog.Builder(this)
            .setTitle("Remove Service?")
            .setMessage("Do you want to remove this service from your liked services?")
            .setPositiveButton("Yes") { dialog, _ ->
                updateLikedServices(false, bookmarkIcon)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateLikedServices(bookmark: Boolean, bookmarkIcon: ImageView) {
        currentUserId?.let { userId ->
            serviceId?.let { sId ->
                val userDocRef = db.collection("users").document(userId)
                val updateOperation = if (bookmark) FieldValue.arrayUnion(sId) else FieldValue.arrayRemove(sId)

                userDocRef.update("likedServices", updateOperation)
                    .addOnSuccessListener {
                        isBookmarked = bookmark
                        updateBookmarkIcon(bookmarkIcon, isBookmarked)
                        val message = if (bookmark) "Service saved!" else "Service removed from saved."
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("SingleServiceActivity", "Error updating liked services: ${e.message}", e)
                        val errorMessage = if (bookmark) "Failed to save service." else "Failed to remove service."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}