package com.example.musicplayer.MusicPlayerScreen.Service

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.Service.AudioState.*
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AudioServiceHandler(
    private val exoPlayer: ExoPlayer
) : Player.Listener {
    private val _playerState = MutableSharedFlow<AudioState>(1 )
    val playerState = _playerState.asSharedFlow()

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
        homeSongAudio: AudioFile,
        audioList: List<AudioFile> = emptyList(),
        seekPosition: Long = 0
    ) {

        when (playerEvent) {
            PlayerEvent.Backward -> exoPlayer.seekBack()
            PlayerEvent.Forward -> exoPlayer.seekForward()
            PlayerEvent.SeekToNext -> exoPlayer.seekToNext()
            PlayerEvent.SeekToPrevious -> exoPlayer.seekToPrevious()
            PlayerEvent.SeekTo -> {
                exoPlayer.seekTo(seekPosition)
            }
            PlayerEvent.ToggleRepeat -> {
                exoPlayer.repeatMode = when (exoPlayer.repeatMode) {
                    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
                    Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_ALL
                    else -> Player.REPEAT_MODE_OFF
                }
            }
            PlayerEvent.PlayPause -> playOrPause()
            PlayerEvent.Stop -> {
                exoPlayer.stop()
                stopProgressUpdate()
            }

            PlayerEvent.SelectedAudioChange -> {
                val mediaItems = audioList.map { it.toMediaItem() }

                val currentQueueIds = List(exoPlayer.mediaItemCount) { i -> exoPlayer.getMediaItemAt(i).mediaId }
                val newQueueIds = mediaItems.map { it.mediaId }

                // 2. Only reload the list if it's actually different (saves memory/time)
                if (currentQueueIds != newQueueIds && mediaItems.isNotEmpty()) {
                    exoPlayer.setMediaItems(mediaItems)
                }

                // 3. Find the ID in the list to get the NEW index
                val index = audioList.indexOfFirst { it.id == homeSongAudio.id }

                if (index != -1) {
                    // Jump to the calculated index
                    exoPlayer.seekTo(index, 0)
                } else if (exoPlayer.mediaItemCount == 0) {
                    // Fallback: If queue is empty, just play this one song
                    exoPlayer.setMediaItem(homeSongAudio.toMediaItem())
                }
                exoPlayer.prepare()
                exoPlayer.play()
                startProgressUpdate()
            }

            is PlayerEvent.OnAudioSongPlay -> {
                exoPlayer.setMediaItem(playerEvent.mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
                startProgressUpdate()
            }

            PlayerEvent.ToggleShuffle -> {
                exoPlayer.shuffleModeEnabled = !exoPlayer.shuffleModeEnabled
            }
            is PlayerEvent.UpdateProgress -> {
                if (exoPlayer.duration > 0) {
                    exoPlayer.seekTo((exoPlayer.duration * playerEvent.newProgress).toLong())
                }
            }
        }



    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
        _playerState.tryEmit(RepeatModeChanged(repeatMode))
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        _playerState.tryEmit(ShuffleModeChanged(shuffleModeEnabled))
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _playerState.tryEmit(Playing(isPlaying))

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
        _playerState.tryEmit(CurrentPlaying(
            mediaItem
        ))
    }
    private fun startProgressUpdate() {
        job?.cancel()
        job = scope.launch {
            while (true) {
                if (exoPlayer.isPlaying) {
                    _playerState.tryEmit(Progress(exoPlayer.currentPosition))
                }
                delay(500.milliseconds)
            }
        }
    }

    private fun stopProgressUpdate() {
        job?.cancel()
    }

    private fun playOrPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
            _playerState.tryEmit(Playing(isPlaying = false))
            stopProgressUpdate()
        } else {
            exoPlayer.prepare()
            exoPlayer.play()
            _playerState.tryEmit(Playing(isPlaying = true))
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
    object ToggleRepeat : PlayerEvent()
    object SeekTo : PlayerEvent()
    object Stop : PlayerEvent()
    object ToggleShuffle : PlayerEvent()
    data class UpdateProgress(val newProgress: Float) : PlayerEvent()
    data class OnAudioSongPlay(val mediaItem : MediaItem) : PlayerEvent()
}

sealed class AudioState {
    object Initial : AudioState()
    data class Ready(val duration: Long) : AudioState()
    data class Progress(val progress: Long) : AudioState()
    data class Buffering(val progress: Long) : AudioState()
    data class RepeatModeChanged(val repeatModeChanged: Int) : AudioState()
    data class ShuffleModeChanged(val isShuffleEnabled: Boolean) : AudioState()
    data class Playing(val isPlaying: Boolean) : AudioState()
    data class CurrentPlaying(val mediaItem: MediaItem?) : AudioState()
}
