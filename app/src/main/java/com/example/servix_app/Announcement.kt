package com.example.servix_app

import com.google.firebase.Timestamp

data class Announcement(
    val images: List<String> = emptyList(),
    val title: String = "",
    val state: String = "",
    val location: String = "",
    val date: Timestamp = Timestamp.now()
)
