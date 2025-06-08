package com.example.servix_app

import com.google.firebase.Timestamp

data class Announcement(
    var id: String = "",
    val title: String? = null,
    val description: String? = null,
    val location: String? = null,
    val images: List<String?> = emptyList(),
    val uid: String? = null,
    val date: Timestamp? = null,
    val likes: Int = 0,
    val category: String? = null
)