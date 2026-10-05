package com.example.musicplayer.localfiles.domain.UseCase

import android.content.Context
import com.example.musicplayer.localfiles.data.ReposistoryImpl.ReposistoryImpl
import com.example.musicplayer.localfiles.domain.Reposistory.localFilesReposistory

class fetchSongUsecase(private val reposistory : localFilesReposistory) {
    operator fun invoke(context: Context) {
        reposistory.fetchlocalAudioFiles(context)
    }
}