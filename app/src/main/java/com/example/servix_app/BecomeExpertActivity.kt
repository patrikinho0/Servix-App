package com.example.servix_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BecomeExpertActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_become_expert)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val expertiseInput = findViewById<EditText>(R.id.expertise_input)
        val descriptionInput = findViewById<EditText>(R.id.description_input)
        val submitButton = findViewById<Button>(R.id.submit_expert_button)

        submitButton.setOnClickListener {
            val expertise = expertiseInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val user = auth.currentUser

            if (user == null) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (expertise.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val expertData = hashMapOf(
                "uid" to user.uid,
                "expertise" to expertise,
                "description" to description,
                "rating" to 0.0,
                "numberOfRatings" to 0
            )

            db.collection("experts").document(user.uid).set(expertData)
                .addOnSuccessListener {
                    db.collection("users").document(user.uid)
                        .update("role", "expert")
                        .addOnSuccessListener {
                            Toast.makeText(this, "You are now an expert!", Toast.LENGTH_LONG).show()
                            startActivity(Intent(this, ProfileActivity::class.java)
                                .putExtra("selected_item_id", R.id.profile))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update user role", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to become expert", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
