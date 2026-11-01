package com.example.mypocketguide.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageBytes: ByteArray?,
    val addedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FavoriteEntity) return false
        return id == other.id &&
                title == other.title &&
                description == other.description &&
                addedAt == other.addedAt &&
                (imageBytes?.contentEquals(other.imageBytes ?: ByteArray(0)) ?: (other.imageBytes == null))
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + addedAt.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        return result
    }
}