package com.example.musicplayer.DI

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.FolderScreen.Data_layer.local.DataSources.FolderLocalDataSource
import com.example.musicplayer.FolderScreen.Data_layer.local.DataSources.FolderLocalDataSourceImpl
import com.example.musicplayer.FolderScreen.Data_layer.local.Database.FoldersDatabase
import org.koin.android.ext.koin.androidContext
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


    single {
        get<FoldersDatabase>().dao
    }
    single<FolderLocalDataSource> {
        FolderLocalDataSourceImpl(get())
    }


}
