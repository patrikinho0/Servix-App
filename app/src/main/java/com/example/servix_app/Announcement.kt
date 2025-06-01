package com.example.servix_app

import com.google.firebase.Timestamp

data class Announcement(
    val images: List<String> = emptyList(),
    val title: String = "",
    val location: String = "",
    val description: String = "",
    val date: Timestamp = Timestamp.now(),
    val uid: String = ""
)

