package com.example.servix_app

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var loadingDialog: Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var signInClient: SignInClient
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private val db = Firebase.firestore

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            val data: Intent? = it.data
            imageUri = data?.data
            imageUri?.let { uri ->
                Picasso.get().load(uri).into(imageView)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?, onSuccess: (String) -> Unit) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val storageReference = FirebaseStorage.getInstance().reference
        val imageRef = storageReference.child("images/${UUID.randomUUID()}")

        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                onSuccess(downloadUri.toString())
            } else {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        signInClient = Identity.getSignInClient(this)

        val pictureAdd: ImageView = findViewById(R.id.plusIcon_imageView)
        val email: EditText = findViewById(R.id.register_email_editText)
        val password: EditText = findViewById(R.id.register_password_editText)
        val registerButton: Button = findViewById(R.id.register_button)
        val goToLogin: TextView = findViewById(R.id.register_signin_textView)
        val googleRegister: LinearLayout = findViewById(R.id.google_layout)
        loadingDialog = createLoadingDialog(this)

        imageView = findViewById(R.id.plusIcon_imageView)

        pictureAdd.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getContent.launch(intent)
        }

        registerButton.setOnClickListener {
            loadingDialog.show()
            if (email.text.toString().isEmpty() || password.text.toString().isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Please select a profile picture", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        loadingDialog.dismiss()
                        val user = auth.currentUser
                        val emailText = email.text.toString()
                        val name = emailText.substringBefore("@")

                        uploadImageToFirebase(imageUri!!) { downloadUrl ->
                            val userData = hashMapOf(
                                "name" to name,
                                "email" to emailText,
                                "role" to "user",
                                "uid" to (user?.uid ?: "null"),
                                "profilePictureUrl" to downloadUrl,
                                "likedServices" to listOf<String>()
                            )

                            db.collection("users")
                                .document(user?.uid ?: "null")
                                .set(userData)
                                .addOnSuccessListener {
                                    val mainIntent = Intent(this, MainActivity::class.java)
                                    startActivity(mainIntent)
                                    finish()
                                }
                                .addOnFailureListener {
                                    loadingDialog.dismiss()
                                    Toast.makeText(baseContext, "Failed to register user.", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        goToLogin.setOnClickListener {
            val goToLoginIntent = Intent(this, LoginActivity::class.java)
            startActivity(goToLoginIntent)
            finish()
        }

        googleRegister.setOnClickListener {
            startGoogleSignIn()
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

    private fun startGoogleSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        signInClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                startIntentSenderForResult(
                    result.pendingIntent.intentSender,
                    GOOGLE_SIGN_IN_REQUEST_CODE,
                    null, 0, 0, 0, null
                )
            }
            .addOnFailureListener {
                Log.e("Google Sign-In", "Google Sign-In failed: ${it.message}")
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken

                if (idToken != null) {
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser

                                val userData = hashMapOf(
                                    "name" to (user?.displayName ?: "Unknown"),
                                    "email" to (user?.email ?: ""),
                                    "role" to "user",
                                    "uid" to (user?.uid ?: "null"),
                                    "profilePictureUrl" to (user?.photoUrl?.toString() ?: ""),
                                    "likedServices" to listOf<String>()
                                )

                                db.collection("users")
                                    .document(user?.uid ?: "null")
                                    .set(userData)
                                    .addOnSuccessListener {
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to register user.", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } catch (e: Exception) {
                Log.e("Google Sign-In", "Error: ${e.message}")
            }
        }
    }

    companion object {
        private const val GOOGLE_SIGN_IN_REQUEST_CODE = 1001
    }
}
