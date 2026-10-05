package com.example.musicplayer.localfiles.ui

import androidx.lifecycle.ViewModel
import com.example.musicplayer.localfiles.domain.UseCase.fetchSongUsecase

class DeviceAudioviewModel(private val fetchSongUsecase: fetchSongUsecase): ViewModel() {
}