package com.example.aios.storage

data class Aim(
    val title: String,
    val description: String,
    val deadline: String,
    var progress: Int = 0
)