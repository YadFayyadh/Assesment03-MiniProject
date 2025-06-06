package com.fayyadh0093.miniproject3.ui.theme.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fayyadh0093.miniproject3.model.Resep
import com.fayyadh0093.miniproject3.network.ApiStatus
import com.fayyadh0093.miniproject3.network.ResepApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Resep>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

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


    fun saveData( name: String, bahan: String,langkah: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ResepApi.service.postResep(name, bahan, langkah, userId
                    )
                retrieveData(userId)
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData(
            "image", "image.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}