package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.ArrayList

class LikedServicesActivity : AppCompatActivity(), OnServiceClickListener {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private lateinit var likedServicesRecyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter
    private val likedAnnouncements = mutableListOf<Announcement>()
    private lateinit var noLikedServicesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_liked_services)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        likedServicesRecyclerView = findViewById(R.id.likedServicesRecyclerView)
        noLikedServicesText = findViewById(R.id.no_liked_services_text)

        myAdapter = MyAdapter(likedAnnouncements, this)
        likedServicesRecyclerView.adapter = myAdapter
        likedServicesRecyclerView.layoutManager = LinearLayoutManager(this)

        val backButton: ImageView = findViewById(R.id.liked_services_back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loadLikedServices()
    }

    override fun onResume() {
        super.onResume()
        loadLikedServices()
    }

    private fun loadLikedServices() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Please log in to view liked services.", Toast.LENGTH_SHORT).show()
            likedAnnouncements.clear()
            myAdapter.updateData(likedAnnouncements)
            noLikedServicesText.visibility = View.VISIBLE
            return
        }

        val userId = currentUser.uid

        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val likedServiceIds = userDoc.get("likedServices") as? List<String> ?: emptyList()

                    if (likedServiceIds.isEmpty()) {
                        likedAnnouncements.clear()
                        myAdapter.updateData(likedAnnouncements)
                        noLikedServicesText.visibility = View.VISIBLE
                        return@addOnSuccessListener
                    }

                    if (likedServiceIds.size > 5) {
                        Log.w("LikedServices", "More than 10 liked services found. Firestore 'whereIn' limit is 10. Consider pagination.")
                    }

                    val servicesToQuery = likedServiceIds.take(5)

                    db.collection("services")
                        .whereIn(com.google.firebase.firestore.FieldPath.documentId(), servicesToQuery)
                        .orderBy("date", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { serviceDocs ->
                            likedAnnouncements.clear()
                            for (document in serviceDocs) {
                                val announcement = document.toObject(Announcement::class.java)
                                announcement.id = document.id
                                likedAnnouncements.add(announcement)
                            }
                            myAdapter.updateData(likedAnnouncements)
                            noLikedServicesText.visibility = if (likedAnnouncements.isEmpty()) View.VISIBLE else View.GONE
                            Log.d("LikedServices", "Loaded ${likedAnnouncements.size} liked services.")
                        }
                        .addOnFailureListener { exception ->
                            Log.w("LikedServices", "Error getting liked service details: ", exception)
                            Toast.makeText(this, "Failed to load liked service details.", Toast.LENGTH_SHORT).show()
                            noLikedServicesText.visibility = View.VISIBLE
                        }
                } else {
                    likedAnnouncements.clear()
                    myAdapter.updateData(likedAnnouncements)
                    noLikedServicesText.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                Log.w("LikedServices", "Error getting user document for liked services: ", exception)
                Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show()
                noLikedServicesText.visibility = View.VISIBLE
            }
    }

    override fun onServiceClick(announcement: Announcement) {
        val intent = Intent(this, SingleServiceActivity::class.java).apply {
            putExtra("serviceId", announcement.id)
            putExtra("title", announcement.title)
            putExtra("description", announcement.description)
            putExtra("date", announcement.date?.toDate()?.let {
                SimpleDateFormat("dd MMM. 'at' hh:mm:ss a", Locale.getDefault()).format(it)
            })
            putExtra("location", announcement.location)
            putExtra("uid", announcement.uid)
            putStringArrayListExtra("images", ArrayList(announcement.images.filterNotNull()))
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}