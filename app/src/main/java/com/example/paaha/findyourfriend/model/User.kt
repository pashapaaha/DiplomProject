package com.example.paaha.findyourfriend.model

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",

    var latitude: Double? = null,
    var longitude: Double? = null,
    var lastLocationUpdate: Long? = null
)