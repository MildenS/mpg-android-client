package com.example.mypocketguide.data.mapper

import android.util.Base64
import com.example.mypocketguide.data.remote.dto.GetExhibitResponseDto
import com.example.mypocketguide.domain.Exhibit

fun GetExhibitResponseDto.toDomain(): Exhibit {
    val bytes = try {
        // Сервер отдаёт base64. Иногда с префиксом "data:image/...;base64," — отрезаем.
        val clean = exhibitImage.substringAfter("base64,", exhibitImage)
        Base64.decode(clean, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }

    return Exhibit(
        id = exhibitId,
        title = exhibitTitle,
        description = exhibitDescription,
        imageBytes = bytes
    )
}