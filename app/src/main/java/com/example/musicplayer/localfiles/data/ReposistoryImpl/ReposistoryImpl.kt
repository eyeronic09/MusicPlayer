package com.example.musicplayer.localfiles.data.ReposistoryImpl

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.musicplayer.localfiles.data.local.localAudioDataSources
import com.example.musicplayer.localfiles.domain.Model.Song
import com.example.musicplayer.localfiles.domain.Reposistory.localFilesReposistory


class ReposistoryImpl(private val localAudioDataSources: localAudioDataSources): localFilesReposistory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun fetchlocalAudioFiles(context: Context) : List<Song> {
        return localAudioDataSources.fetchlocalAudioFiles(context)
    }
}