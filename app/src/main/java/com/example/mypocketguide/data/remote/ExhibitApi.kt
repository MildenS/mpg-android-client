package com.example.mypocketguide.data.remote

import com.example.mypocketguide.data.remote.dto.GetExhibitResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ExhibitApi {

    @Multipart
    @POST("get-exhibit")
    suspend fun getExhibit(
        @Part image: MultipartBody.Part
    ): GetExhibitResponseDto
}