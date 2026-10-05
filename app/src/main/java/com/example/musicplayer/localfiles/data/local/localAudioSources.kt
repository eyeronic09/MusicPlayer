package com.example.musicplayer.localfiles.data.local

import android.content.Context
import com.example.musicplayer.localfiles.domain.Model.Song
import kotlinx.coroutines.flow.Flow

interface localAudioDataSources {
    fun fetchlocalAudioFiles(context: Context): List<Song>
}
