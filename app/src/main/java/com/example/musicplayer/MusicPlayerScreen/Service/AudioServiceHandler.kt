package com.example.musicplayer.MusicPlayerScreen.Service

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.MusicPlayerScreen.Service.AudioState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AudioServiceHandler(
    private val exoPlayer: ExoPlayer
) : Player.Listener {
    private val _playerState = MutableStateFlow<AudioState>(AudioState.Initial)
    val playerState = _playerState.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        exoPlayer.addListener(this)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItems(mediaItems: List<MediaItem>) {
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
    }

    suspend fun onPlayerEvents(
        playerEvent: PlayerEvent,
        selectedAudioIndex: Int = -1,
        seekPosition: Long = 0
    ) {
        when (playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.SeekToPrevious -> exoPlayer.seekToPrevious()
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.Stop -> {
                exoPlayer.stop()
                stopProgressUpdate()
            }
            PlayerEvent.SelectedAudioChange -> {
                if (exoPlayer.currentMediaItemIndex != selectedAudioIndex) {
                    exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                    _playerState.value = Playing(isPlaying = true)
                    exoPlayer.play()
                    startProgressUpdate()
                }
            }
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            is PlayerEvent.UpdateProgress -> {
                exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _playerState.value = AudioState.Ready(exoPlayer.duration)
            }

            Player.STATE_BUFFERING -> {
                _playerState.value = AudioState.Buffering(exoPlayer.currentPosition)
            }

            Player.STATE_ENDED -> {}
            Player.STATE_IDLE -> {
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _playerState.value = AudioState.Playing(isPlaying)
        _playerState.value = AudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
        if (isPlaying) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    private fun startProgressUpdate() {
        job?.cancel()
        job = scope.launch {
            while (true) {
                _playerState.value = AudioState.Progress(exoPlayer.currentPosition)
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
        _playerState.value = AudioState.Playing(isPlaying = false)
    }

    private fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            stopProgressUpdate()
        } else {
            exoPlayer.play()
            _playerState.value = AudioState.Playing(isPlaying = true)
            startProgressUpdate()
        }
    }
}

sealed class PlayerEvent {
    object PlayPause : PlayerEvent()
    object SelectedAudioChange : PlayerEvent()
    object Backward : PlayerEvent()
    object SeekToNext : PlayerEvent()
    object SeekToPrevious : PlayerEvent()
    object Forward : PlayerEvent()
    object SeekTo : PlayerEvent()
    object Stop : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
}

sealed class AudioState {
    object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class CurrentPlaying(val mediaItemIndex: Int) : AudioState()
}
