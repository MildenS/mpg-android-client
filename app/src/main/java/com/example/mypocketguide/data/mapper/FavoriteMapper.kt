package com.example.mypocketguide.data.mapper

import com.example.mypocketguide.data.local.FavoriteEntity
import com.example.mypocketguide.domain.Exhibit

fun FavoriteEntity.toDomain(): Exhibit = Exhibit(
    id = id,
    title = title,
    description = description,
    imageBytes = imageBytes,
    viewedAt = addedAt
)

fun Exhibit.toFavoriteEntity(): FavoriteEntity = FavoriteEntity(
    id = id,
    title = title,
    description = description,
    imageBytes = imageBytes,
    addedAt = System.currentTimeMillis()
)