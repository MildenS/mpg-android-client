package com.example.mypocketguide.data.repository

import com.example.mypocketguide.data.local.HistoryDao
import com.example.mypocketguide.data.mapper.toDomain
import com.example.mypocketguide.data.mapper.toEntity
import com.example.mypocketguide.domain.Exhibit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HistoryRepository(private val dao: HistoryDao) {

    fun observeHistory(): Flow<List<Exhibit>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    suspend fun add(exhibit: Exhibit) = withContext(Dispatchers.IO) {
        dao.upsert(
            exhibit.copy(viewedAt = System.currentTimeMillis()).toEntity()
        )
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        dao.clear()
    }
}