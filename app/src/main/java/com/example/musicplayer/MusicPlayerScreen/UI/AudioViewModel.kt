package com.example.musicplayer.MusicPlayerScreen.UI

import androidx.lifecycle.ViewModel
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler

class AudioViewModel(
    private val audioServiceHandler: AudioServiceHandler
) : ViewModel() {
    val playerState = audioServiceHandler.playerState
}
