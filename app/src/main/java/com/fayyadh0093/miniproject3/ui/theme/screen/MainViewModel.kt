package com.fayyadh0093.miniproject3.ui.theme.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fayyadh0093.miniproject3.model.Resep
import com.fayyadh0093.miniproject3.network.ApiStatus
import com.fayyadh0093.miniproject3.network.ImgbbApi
import com.fayyadh0093.miniproject3.network.ResepApi
import com.fayyadh0093.miniproject3.network.ResepUpdate
import com.fayyadh0093.miniproject3.util.ImgurApi
import com.fayyadh0093.miniproject3.util.ImgurResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.FormBody.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.ByteArrayOutputStream
import java.io.IOException

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Resep>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    private val clientId = "0c7939f538315f9"

//    var selectedHewan = mutableStateOf<Resep?>(null)
//        private set

//    fun setSelectedHewan(resep: Resep) {
//        selectedHewan.value = hew
//    }
//
//    fun clearSelectedHewan() {
//        selectedHewan.value = null
//    }

    fun retrieveData(userId: String?){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val allResep = ResepApi.service.getResepAll()
                Log.d("MainViewModel", "allResep: $allResep")

                // Dummy data = resep yang userId-nya kosong
                val dummyData = allResep.filter { it.userId.isNullOrBlank() }
                Log.d("MainViewModel", "dummyData: $dummyData")

                val finalList = if (userId.isNullOrBlank()) {
                    // Belum login: hanya dummy data
                    dummyData
                } else {
                    // Sudah login: tambahkan data user kalau ada
                    val userData = try {
                        ResepApi.service.getResep(userId)
                    } catch (e: Exception) {
                        Log.d("MainViewModel", "getResep failed: ${e.message}")
                        emptyList()
                    }

                    if (userData.isNullOrEmpty()) {
                        // Kalau user belum pernah nambah data: tetap dummy data
                        dummyData
                    } else {
                        // Dummy data + data user
                        dummyData + userData
                    }
                }

                Log.d("MainViewModel", "finalList: $finalList")
                data.value = finalList
                status.value = ApiStatus.SUCCES
            }catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun deleteData(userId: String,resepId: String ){
        viewModelScope.launch(Dispatchers.IO){
            try {
                val result = ResepApi.service.deleteResep(resepId)
                    retrieveData(userId)
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun updateData(
        id: String,
        name: String,
        bahan: String,
        langkah: String,
        userId: String,
        imageUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ResepApi.service.updateResep(id, name, bahan, langkah, userId, imageUrl)
                retrieveData(userId)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Update gagal: ${e.message}")
                errorMessage.value = "Gagal update: ${e.message}"
            }
        }
    }



    fun saveData( name: String, bahan: String,langkah: String, userId: String, imageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ResepApi.service.postResep(name, bahan, langkah, userId, imageUrl
                    )
                retrieveData(userId)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun bitmapToMultipartBody(bitmap: Bitmap): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", "upload.jpg", requestBody)
    }


    fun clearMessage() {
        errorMessage.value = null
    }


    suspend fun uploadImageToImgBBViaRetrofit(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val imageBytes = byteArrayOutputStream.toByteArray()

        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("image", "upload.jpg", requestBody)

        return try {
            val response = ImgbbApi.services.uploadImage(image = multipart)
            if (response.success) {
                response.data.url
            } else {
                Log.e("UploadError", "Upload gagal: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UploadError", "Exception: ${e.message}")
            null
        }
    }

    fun uploadAndSave(
        name: String,
        bahan: String,
        langkah: String,
        userId: String,
        bitmap: Bitmap,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val imageUrl = uploadImageToImgBBViaRetrofit(bitmap)
            if (imageUrl != null) {
                saveData(name, bahan, langkah, userId, imageUrl)
                onSuccess()
            } else {
                onError("Gagal upload gambar ke ImgBB")
            }
        }
    }



}

