package com.example.servix_app

import com.google.firebase.Timestamp

data class Expert(
    val uid: String = "",
    val name: String = "",
    val expertise: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val numberOfRatings: Int = 0,
    val profilePictureUrl: String = "",
    val date: Timestamp? = null
)