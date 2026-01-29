package com.example.musicplayer.DI

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.FolderScreen.Data_layer.local.DataSources.FolderLocalDataSource
import com.example.musicplayer.FolderScreen.Data_layer.local.DataSources.FolderLocalDataSourceImpl
import com.example.musicplayer.FolderScreen.Data_layer.local.Database.FoldersDatabase
import com.example.musicplayer.FolderScreen.Data_layer.local.Repository.FolderRepositoryImpl
import com.example.musicplayer.FolderScreen.Domain_layer.repostiory.FolderRepository
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.FolderScreenVM
import com.example.musicplayer.PlayerScreen.PlayerScreenVm
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}

val appModule: Module = module {
    single {
        ExoPlayer.Builder(androidContext()).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    viewModel { FolderScreenVM(get() ,  androidContext() )}
    viewModel { PlayerScreenVm(get() , Application() ) }

    single {
        FoldersDatabase.getIntance(androidContext())
    }

    single {
        get<FoldersDatabase>().dao
    }

    single<FolderLocalDataSource> {
        FolderLocalDataSourceImpl(get())
    }

    single<FolderRepository> {
        FolderRepositoryImpl(get())
    }
}
