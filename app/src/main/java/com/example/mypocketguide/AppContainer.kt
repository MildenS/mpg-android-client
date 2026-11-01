package com.example.mypocketguide

import android.content.Context
import com.example.mypocketguide.data.local.ExhibitDatabase
import com.example.mypocketguide.data.remote.NetworkModule
import com.example.mypocketguide.data.repository.ExhibitRepository
import com.example.mypocketguide.data.repository.FavoritesRepository
import com.example.mypocketguide.data.repository.HistoryRepository
import com.example.mypocketguide.data.settings.SettingsRepository

class AppContainer(context: Context) {

    private val appContext = context.applicationContext
    private val database = ExhibitDatabase.get(appContext)

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(appContext)
    }

    val exhibitRepository: ExhibitRepository by lazy {
        ExhibitRepository(apiProvider = { NetworkModule.getApi() })
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepository(database.historyDao())
    }

    val favoritesRepository: FavoritesRepository by lazy {
        FavoritesRepository(database.favoriteDao())
    }
}