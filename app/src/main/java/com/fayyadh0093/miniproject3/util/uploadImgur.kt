package com.fayyadh0093.miniproject3.util

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.ByteArrayOutputStream

fun uploadToImgur(
    bitmap: Bitmap,
    clientId: String,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit
) {
    kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
        try {
            val client = OkHttpClient()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image",
                    null,
                    RequestBody.create("image/*".toMediaTypeOrNull(), imageBytes)
                )
                .build()

            val request = Request.Builder()
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .addHeader("Authorization", "Client-ID $clientId")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val link = JSONObject(json).getJSONObject("data").getString("link")
                onSuccess(link)
            } else {
                onError("Upload gagal: ${response.message}")
            }
        } catch (e: Exception) {
            onError("Error: ${e.message}")
        }
    }
}
