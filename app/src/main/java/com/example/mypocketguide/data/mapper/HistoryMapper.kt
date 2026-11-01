package com.example.mypocketguide.data.mapper

import com.example.mypocketguide.data.local.HistoryEntity
import com.example.mypocketguide.domain.Exhibit

fun HistoryEntity.toDomain(): Exhibit = Exhibit(
    id = id,
    title = title,
    description = description,
    imageBytes = imageBytes,
    viewedAt = viewedAt
)

fun Exhibit.toEntity(): HistoryEntity = HistoryEntity(
    id = id,
    title = title,
    description = description,
    imageBytes = imageBytes,
    viewedAt = viewedAt
)