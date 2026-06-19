package com.example.musicplayer.DI

import android.app.Application
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.example.musicplayer.HomeScreen.Playlist.data.local.ReposistoryImpl.RepositoryImpl as PlaylistRepositoryImpl
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.Playlist.ui.PlayListScreenViewModel
import com.example.musicplayer.HomeScreen.Playlist.ui.PlaylistAllSongViewModel
import com.example.musicplayer.HomeScreen.data.Reposistory.ReposistoryImpl
import com.example.musicplayer.HomeScreen.data.local.database.SONG_DB
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
        // Room Database
        single {
            Room.databaseBuilder(
                androidContext(),
                SONG_DB::class.java,
                "song_db"
            ).fallbackToDestructiveMigration()
                .build()
        }

        // DAO
        single { get<SONG_DB>().dao() }
        single { get<SONG_DB>().playlistDao() }

        // Repositories
        single<MusicRepository> { ReposistoryImpl(androidContext() , get()) }
        single<PlaylistRepository> { PlaylistRepositoryImpl(get()) }
        
        single {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()

            ExoPlayer.Builder(androidContext())
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build()
        }
        
        single {
            AudioServiceHandler(get())
        }

        single {
            NotificationManager(androidContext(), get())
        }
        
        // ViewModels
        viewModel {
            HomeScreenViewModel(get() , get())
        }

        viewModel {
            PlayListScreenViewModel(get())
        }

        viewModel { parameters ->
            PlaylistAllSongViewModel(
                playlistId = parameters.get(),
                playlistRepository = get()
            )
        }

        viewModel {
            MusicViewModel(
                audioService = get(),
                repository = get(),
                playlist = get(),
                context = androidContext(),
                saveStateHandler = get()
            )
        }
    }
}
