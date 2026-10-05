package com.example.musicplayer.localfiles.domain.Reposistory

import android.content.Context
import com.example.musicplayer.localfiles.domain.Model.Song

interface localFilesReposistory {
    fun fetchlocalAudioFiles (context: Context): List<Song>
}