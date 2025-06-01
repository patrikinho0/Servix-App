package com.example.servix_app

import com.google.firebase.Timestamp

data class Announcement(
    var id: String = "",
    val images: List<String?> = emptyList(),
    val title: String? = null,
    val location: String? = null,
    val description: String? = null,
    val date: Timestamp? = null,
    val uid: String? = null
)