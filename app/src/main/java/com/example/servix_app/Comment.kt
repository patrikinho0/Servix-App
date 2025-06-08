package com.example.servix_app

import com.google.firebase.Timestamp

data class Comment(
    var id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val userProfilePictureUrl: String = "",
    val userRole: String = ""
)
