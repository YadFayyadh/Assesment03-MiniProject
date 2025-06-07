package com.fayyadh0093.miniproject3.network

data class ImageBBResponse(
    val data: ImageData,
    val success: Boolean,
    val status: Int
)

data class ImageData(
    val url: String,
    val display_url: String,
    val image: ImageInfo
)

data class ImageInfo(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)
