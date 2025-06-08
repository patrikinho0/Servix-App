package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText // Import EditText
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
import com.google.firebase.Timestamp // Import Timestamp for comment timestamps
import com.google.firebase.firestore.Query // Import Query for orderBy

class SingleServiceActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private var currentUserId: String? = null
    private var serviceId: String? = null
    private var isBookmarked: Boolean = false

    private lateinit var imageIndicatorContainer: LinearLayout
    private lateinit var imageUrls: ArrayList<String>
    private lateinit var bookmarkIcon: ImageView

    private lateinit var globalLikesTextView: TextView

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var postCommentButton: Button
    private lateinit var commentsAdapter: CommentAdapter
    private val commentsList = mutableListOf<Comment>()

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

        val imageRecyclerView = findViewById<RecyclerView>(R.id.imageRecyclerView)
        bookmarkIcon = findViewById<ImageView>(R.id.bookmarkIcon)
        val titleText = findViewById<TextView>(R.id.serviceTitle)
        val descText = findViewById<TextView>(R.id.serviceDescription)
        val dateText = findViewById<TextView>(R.id.serviceDate)
        val locationText = findViewById<TextView>(R.id.serviceLocation)
        val contactButton = findViewById<Button>(R.id.contactButton)
        val authorText = findViewById<TextView>(R.id.serviceAuthor)
        imageIndicatorContainer = findViewById(R.id.imageIndicatorContainer)

        globalLikesTextView = findViewById(R.id.list_item_likes)

        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        commentEditText = findViewById(R.id.commentEditText)
        postCommentButton = findViewById(R.id.postCommentButton)

        commentsAdapter = CommentAdapter(commentsList)
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
        commentsRecyclerView.adapter = commentsAdapter


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
        imageRecyclerView.layoutManager = layoutManager
        imageRecyclerView.adapter = ImageUrlAdapter(imageUrls)

        setupImageIndicator(imageUrls.size)

        imageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        Toast.makeText(this@SingleServiceActivity, "Failed to load author details.", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            authorText.text = "Author: Unknown (No UID)"
            contactButton.setOnClickListener {
                Toast.makeText(this, "Cannot contact: No author UID available.", Toast.LENGTH_SHORT).show()
            }
        }

        if (currentUserId != null && serviceId != null) {
            checkBookmarkStatus()
            loadGlobalLikes()
            loadComments()
            bookmarkIcon.setOnClickListener {
                if (isBookmarked) {
                    showUnbookmarkConfirmationDialog()
                } else {
                    showBookmarkConfirmationDialog()
                }
            }

            postCommentButton.setOnClickListener {
                postComment()
            }
        } else {
            bookmarkIcon.visibility = ImageView.GONE
            Toast.makeText(this, "Login to save services and comment.", Toast.LENGTH_LONG).show()
            globalLikesTextView.visibility = View.GONE
            commentEditText.visibility = View.GONE
            postCommentButton.visibility = View.GONE
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

    private fun checkBookmarkStatus() {
        currentUserId?.let { userId ->
            serviceId?.let { sId ->
                db.collection("users").document(userId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val likedServices = documentSnapshot.get("likedServices") as? List<String>
                            isBookmarked = likedServices?.contains(sId) == true
                            updateBookmarkIcon(isBookmarked)
                        } else {
                            isBookmarked = false
                            updateBookmarkIcon(false)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("SingleServiceActivity", "Error checking bookmark status: ${e.message}", e)
                        isBookmarked = false
                        updateBookmarkIcon(false)
                        Toast.makeText(this, "Error loading bookmark status.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateBookmarkIcon(bookmarked: Boolean) {
        if (bookmarked) {
            bookmarkIcon.setImageResource(R.drawable.bookmark_ic_filled)
        } else {
            bookmarkIcon.setImageResource(R.drawable.bookmark_ic)
        }
    }

    private fun showBookmarkConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Save Service?")
            .setMessage("Do you want to save this service to your liked services?")
            .setPositiveButton("Yes") { dialog, _ ->
                updateLikedServices(true)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showUnbookmarkConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Remove Service?")
            .setMessage("Do you want to remove this service from your liked services?")
            .setPositiveButton("Yes") { dialog, _ ->
                updateLikedServices(false)
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun updateLikedServices(bookmark: Boolean) {
        currentUserId?.let { userId ->
            serviceId?.let { sId ->
                val userDocRef = db.collection("users").document(userId)
                val serviceDocRef = db.collection("services").document(sId)

                val userUpdateOperation = if (bookmark) FieldValue.arrayUnion(sId) else FieldValue.arrayRemove(sId)
                val globalLikesUpdateOperation = if (bookmark) FieldValue.increment(1) else FieldValue.increment(-1)

                db.runBatch { batch ->
                    batch.update(userDocRef, "likedServices", userUpdateOperation)
                    batch.update(serviceDocRef, "likes", globalLikesUpdateOperation)
                }.addOnSuccessListener {
                    isBookmarked = bookmark
                    updateBookmarkIcon(isBookmarked)
                    val message = if (bookmark) "Service saved!" else "Service removed from saved."
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    loadGlobalLikes()
                }.addOnFailureListener { e ->
                    Log.e("SingleServiceActivity", "Error updating liked services: ${e.message}", e)
                    val errorMessage = if (bookmark) "Failed to save service." else "Failed to remove service."
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                    checkBookmarkStatus()
                }
            }
        }
    }

    private fun loadGlobalLikes() {
        serviceId?.let { sId ->
            db.collection("services").document(sId).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val likes = documentSnapshot.getLong("likes")?.toInt() ?: 0
                        globalLikesTextView.text = "\u2764\ufe0f $likes"
                    } else {
                        globalLikesTextView.text = "\u2764\ufe0f 0"
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("SingleServiceActivity", "Error loading global likes: ${e.message}", e)
                    globalLikesTextView.text = "\u2764\ufe0f --"
                }
        }
    }

    private fun loadComments() {
        serviceId?.let { sId ->
            db.collection("services").document(sId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("SingleServiceActivity", "Listen for comments failed.", e)
                        Toast.makeText(this, "Error loading comments.", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    if (snapshots != null && !snapshots.isEmpty) {
                        val newComments = ArrayList<Comment>()
                        for (doc in snapshots.documents) {
                            val comment = doc.toObject(Comment::class.java)
                            comment?.let {
                                it.id = doc.id
                                newComments.add(it)
                            }
                        }
                        commentsAdapter.setComments(newComments)
                    } else if (snapshots != null && snapshots.isEmpty) {
                        commentsAdapter.setComments(emptyList())
                        Log.d("SingleServiceActivity", "No comments found for service $sId.")
                    }
                }
        }
    }

    private fun postComment() {
        val commentText = commentEditText.text.toString().trim()

        if (commentText.isEmpty()) {
            Toast.makeText(this, "Comment cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "You need to be logged in to comment.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                val userName = userDoc.getString("name") ?: "Anonymous User"
                val userProfilePictureUrl = userDoc.getString("profilePictureUrl") ?: ""
                val userRole = userDoc.getString("role") ?: ""

                serviceId?.let { sId ->
                    val newComment = Comment(
                        userId = userId,
                        userName = userName,
                        text = commentText,
                        timestamp = Timestamp.now(),
                        userProfilePictureUrl = userProfilePictureUrl,
                        userRole = userRole
                    )

                    db.collection("services").document(sId)
                        .collection("comments")
                        .add(newComment)
                        .addOnSuccessListener { documentReference ->
                            Log.d("SingleServiceActivity", "Comment added with ID: ${documentReference.id}")
                            commentEditText.text.clear()
                            Toast.makeText(this, "Comment posted!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.w("SingleServiceActivity", "Error adding comment", e)
                            Toast.makeText(this, "Failed to post comment.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("SingleServiceActivity", "Error fetching user info for comment: ${e.message}", e)
                Toast.makeText(this, "Error getting user info. Cannot post comment.", Toast.LENGTH_SHORT).show()
            }
    }
}
