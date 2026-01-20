package com.example.musicplayer.DI

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import com.example.musicplayer.HomeScreen.Data_layer.local.DataSources.FolderLocalDataSource
import com.example.musicplayer.HomeScreen.Data_layer.local.DataSources.FolderLocalDataSourceImpl
import com.example.musicplayer.HomeScreen.Data_layer.local.Database.Folder_DB
import com.example.musicplayer.HomeScreen.SongPlayerVM
import org.koin.android.ext.koin.androidApplication
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

    viewModel {
        SongPlayerVM(get())
    }
    single {
        databaseBuilder(
            context = androidApplication(),
            klass = Folder_DB::
            class.java,
            name = "Audio_File"
        ).build()
    }

    single {
        get<Folder_DB>().dao
    }
    single<FolderLocalDataSource> {
        FolderLocalDataSourceImpl(get())
    }
    single {
        SongPlayerVM(get())
    }


}
