package com.example.mypocketguide.data.repository

import com.example.mypocketguide.data.local.FavoriteDao
import com.example.mypocketguide.data.mapper.toDomain
import com.example.mypocketguide.data.mapper.toFavoriteEntity
import com.example.mypocketguide.domain.Exhibit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavoritesRepository(private val dao: FavoriteDao) {

    fun observeFavorites(): Flow<List<Exhibit>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    fun observeIsFavorite(id: String): Flow<Boolean> =
        dao.observeIsFavorite(id)

    suspend fun add(exhibit: Exhibit) = withContext(Dispatchers.IO) {
        dao.add(exhibit.toFavoriteEntity())
    }

    suspend fun remove(id: String) = withContext(Dispatchers.IO) {
        dao.remove(id)
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        dao.clear()
    }
}