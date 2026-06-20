package com.example.musicplayer.HomeScreen.Playlist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayList (
    val playlistId: Long = 0,
    val playListName: String
) : Parcelable