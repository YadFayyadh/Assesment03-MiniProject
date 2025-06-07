package com.fayyadh0093.miniproject3.util

import com.squareup.moshi.Json

data class ImgurResponse(
    @Json(name = "data") val data: ImgurData,
    @Json(name = "success") val success: Boolean,
    @Json(name = "status") val status: Int
)

data class ImgurData(
    @Json(name = "link") val link: String
)