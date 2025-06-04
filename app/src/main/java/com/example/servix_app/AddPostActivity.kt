package com.example.servix_app

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPostActivity : AppCompatActivity() {

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var selectImagesButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var locationEditText: EditText
    private lateinit var addServiceButton: Button

    private val selectedImages: MutableList<Uri> = mutableListOf()
    private lateinit var imageAdapter: ImageAdapter

    private val IMAGE_PICK_CODE = 1000

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        imageRecyclerView = findViewById(R.id.add_post_imageRecyclerView)
        selectImagesButton = findViewById(R.id.add_post_selectImagesButton)
        addServiceButton = findViewById(R.id.add_post_addServiceButton)

        titleEditText = findViewById(R.id.add_post_titleEditText)
        descriptionEditText = findViewById(R.id.add_post_descriptionEditText)
        locationEditText = findViewById(R.id.add_post_locationEditText)

        imageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        imageAdapter = ImageAdapter(selectedImages as ArrayList<Uri>)
        imageRecyclerView.adapter = imageAdapter

        imageAdapter.setOnDeleteClickListener { position ->
            Toast.makeText(this, "Image removed!", Toast.LENGTH_SHORT).show()
        }

        selectImagesButton.setOnClickListener {
            openGallery()
        }

        addServiceButton.setOnClickListener {
            addService()
        }

        loadingDialog = createLoadingDialog(this)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun addService() {
        val title = titleEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val location = locationEditText.text.toString()

        if (title.isEmpty() || description.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImages.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.show()

        val imageUrls = mutableListOf<String>()
        val uploadTasks = selectedImages.map { imageUri ->
            uploadImageToFirebase(imageUri) { downloadUrl ->
                imageUrls.add(downloadUrl)
                if (imageUrls.size == selectedImages.size) {
                    saveServiceToFirestore(title, description, location, imageUrls)
                }
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri, onSuccess: (String) -> Unit) {
        val imageRef = storage.child("service_images/${UUID.randomUUID()}")

        imageRef.putFile(imageUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                imageRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    onSuccess(downloadUri.toString())
                } else {
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                    loadingDialog.dismiss()
                }
            }
    }

    private fun saveServiceToFirestore(title: String, description: String, location: String, imageUrls: List<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val serviceData = hashMapOf(
            "title" to title,
            "description" to description,
            "location" to location,
            "images" to imageUrls,
            "uid" to userId,
            "date" to Date(),
            "category" to "service"
        )

        db.collection("services")
            .add(serviceData)
            .addOnSuccessListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                loadingDialog.dismiss()
                Toast.makeText(this, "Failed to add service", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                val count = data.clipData!!.itemCount
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    selectedImages.add(imageUri)
                }
            } else {
                val imageUri = data?.data
                if (imageUri != null) {
                    selectedImages.add(imageUri)
                }
            }

            imageAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createLoadingDialog(context: Context): Dialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.loading_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}