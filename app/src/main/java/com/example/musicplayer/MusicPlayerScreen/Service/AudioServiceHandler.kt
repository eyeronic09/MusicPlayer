package com.example.musicplayer.MusicPlayerScreen.Service

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
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
    private val _playerState = MutableStateFlow<AudioState>(Initial)
    val playerState = _playerState.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        exoPlayer.addListener(this)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        exoPlayer.addMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    fun setMediaItems(mediaItems: List<MediaItem> , playWhenReady : Boolean = false) {
        Log.d("AudioServiceHandler", "Setting ${mediaItems.size} media items")
        exoPlayer.setMediaItems(mediaItems)
        exoPlayer.prepare()
        if (playWhenReady){
            exoPlayer.play()
        }
    }

    fun onPlayerEvents(
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
                if (selectedAudioIndex != -1) {
                    Log.d("AudioServiceHandler", "Changing audio to index: $selectedAudioIndex")
                    val mediaItem = exoPlayer.getMediaItemAt(selectedAudioIndex)
                    Log.d("AudioServiceHandler", "Media URI: ${mediaItem.localConfiguration?.uri}")
                    
                    exoPlayer.seekToDefaultPosition(selectedAudioIndex)
                    exoPlayer.playWhenReady = true
                    exoPlayer.prepare() // Ensure preparedL
                    exoPlayer.play()
                    _playerState.value = Playing(isPlaying = true)
                    startProgressUpdate()
                }
            }
            PlayerEvent.SeekTo -> exoPlayer.seekTo(seekPosition)
            is PlayerEvent.UpdateProgress -> {
                if (exoPlayer.duration > 0) {
                    exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
                }
            }

            is PlayerEvent.OnAudioSongPlay -> {
                exoPlayer.setMediaItem(playerEvent.mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
                _playerState.value = Playing(isPlaying = true)
                startProgressUpdate()
            }
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                Log.d("AudioServiceHandler", "Player Ready, duration: ${exoPlayer.duration}")
                _playerState.value = Ready(exoPlayer.duration)
            }
            Player.STATE_BUFFERING -> {
                _playerState.value = Buffering(exoPlayer.currentPosition)
            }
            Player.STATE_ENDED -> {
                _playerState.value = Playing(isPlaying = false)
            }
            Player.STATE_IDLE -> {
                Log.d("AudioServiceHandler", "Player Idle")
            }
        }
    }
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("AudioServiceHandler", "Is Playing: $isPlaying")
        _playerState.value = Playing(isPlaying)
        _playerState.value = CurrentPlaying(exoPlayer.currentMediaItem)
        if (isPlaying) {
            startProgressUpdate()
        } else {
            stopProgressUpdate()
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("AudioServiceHandler", "Player Error: ${error.errorCodeName} (${error.errorCode})", error)
        Log.e("AudioServiceHandler", "Failing URI: ${exoPlayer.currentMediaItem?.localConfiguration?.uri}")
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        _playerState.value = CurrentPlaying(mediaItem)
    }
    private fun startProgressUpdate() {
        job?.cancel()
        job = scope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playerState.value = Progress(exoPlayer.currentPosition)
                }
                delay(1000)
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
    }

    private fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.value = Playing(isPlaying = false)
            stopProgressUpdate()
        } else {
            exoPlayer.prepare()
            exoPlayer.play()
            _playerState.value = Playing(isPlaying = true)
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
    data class OnAudioSongPlay(val mediaItem : MediaItem) : PlayerEvent()
}

sealed class AudioState {
    object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class CurrentPlaying(val mediaItems: MediaItem? ) : AudioState()
}
