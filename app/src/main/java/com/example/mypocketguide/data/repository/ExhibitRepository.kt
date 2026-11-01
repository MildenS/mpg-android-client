package com.example.mypocketguide.data.repository

import com.example.mypocketguide.data.mapper.toDomain
import com.example.mypocketguide.data.remote.ExhibitApi
import com.example.mypocketguide.domain.Exhibit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ExhibitRepository(
    private val apiProvider: () -> ExhibitApi
) {

    suspend fun recognizeExhibit(imageBytes: ByteArray): Result<Exhibit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = imageBytes.toRequestBody("image/jpeg".toMediaType())
                val part = MultipartBody.Part.createFormData(
                    name = "exhibit-image",
                    filename = "exhibit.jpg",
                    body = body
                )
                apiProvider().getExhibit(part).toDomain()
            }
        }
}