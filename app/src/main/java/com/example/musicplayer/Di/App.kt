package com.example.musicplayer.Di

import android.app.Application
import com.example.musicplayer.localfiles.data.ReposistoryImpl.ReposistoryImpl
import com.example.musicplayer.localfiles.data.local.localAudioDataSources
import com.example.musicplayer.localfiles.data.local.localAudioSourcesImpl
import com.example.musicplayer.localfiles.domain.Reposistory.localFilesReposistory
import com.example.musicplayer.localfiles.domain.UseCase.fetchSongUsecase
import com.example.musicplayer.localfiles.ui.DeviceAudioviewModel
import org.koin.core.KoinApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MainApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger(Level.DEBUG)
            androidContext(this@MainApplication)
            modules(appModule)
        }
    }
    val appModule = module {
        single <localFilesReposistory>{
            ReposistoryImpl(get())
        }
        single <localAudioDataSources> {
            localAudioSourcesImpl(get())
        }

        single {
            fetchSongUsecase(get())
        }
        viewModel {
            DeviceAudioviewModel(get())
        }
    }
}