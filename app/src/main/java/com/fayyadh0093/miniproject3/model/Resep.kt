package com.fayyadh0093.miniproject3.model

data class Resep(
    val id: String,
    val name: String,
    val bahan: String,
    val langkah: String,
    val userId: String,
    val imageUrl: String,
    val mine: Int
)
