package com.example.mypocketguide.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageBytes: ByteArray?,
    val viewedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HistoryEntity) return false
        return id == other.id &&
                title == other.title &&
                description == other.description &&
                viewedAt == other.viewedAt &&
                (imageBytes?.contentEquals(other.imageBytes ?: ByteArray(0)) ?: (other.imageBytes == null))
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + viewedAt.hashCode()
        result = 31 * result + (imageBytes?.contentHashCode() ?: 0)
        return result
    }
}