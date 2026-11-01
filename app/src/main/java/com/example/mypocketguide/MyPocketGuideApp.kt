package com.example.mypocketguide

import android.app.Application
import com.example.mypocketguide.data.remote.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MyPocketGuideApp : Application() {

    val container: AppContainer by lazy { AppContainer(this) }

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // Подгружаем сохранённый URL сервера в NetworkModule
        appScope.launch {
            val url = container.settingsRepository.serverUrl.first()
            NetworkModule.setBaseUrl(url)
        }
    }
}