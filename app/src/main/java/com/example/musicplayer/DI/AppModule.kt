package com.example.musicplayer.DI

import android.app.Application
import com.example.musicplayer.HomeScreen.data.Reposistory.ReposistoryImpl
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.HomeScreen.ui.HomeScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class AppModule : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppModule)
            modules(appModule)
        }

    }
    val appModule = module {
        single<MusicRepository> { ReposistoryImpl(get()) }
        viewModel {
            HomeScreenViewModel(get())
        }

    }
}