package com.example.musicplayer.DI

import android.app.Application
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.HomeScreen.data.Reposistory.ReposistoryImpl
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.HomeScreen.ui.HomeScreenViewModel
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler
import com.example.musicplayer.MusicPlayerScreen.UI.MusicViewModel
import com.example.musicplayer.MusicPlayerScreen.notification.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class AppModule : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppModule)
            modules(appModule)
        }
    }

    private val appModule = module {
        single<MusicRepository> { ReposistoryImpl(get()) }
        
        single {
            ExoPlayer.Builder(androidContext()).build()
        }
        
        single {
            AudioServiceHandler(get())
        }

        single {
            NotificationManager(androidContext(), get())
        }
        
        viewModel {
            HomeScreenViewModel(get())
        }

        viewModel {
            MusicViewModel(
                audioService = get(),
                repository = get(),
                saveStateHandler = get()
            )
        }
    }
}
