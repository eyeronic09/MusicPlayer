package com.example.musicplayer.MusicPlayerScreen.UI

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.domain.reposistory.PlaylistRepository
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository
import com.example.musicplayer.HomeScreen.ui.HomeUiEffect
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler
import com.example.musicplayer.MusicPlayerScreen.Service.AudioState
import com.example.musicplayer.MusicPlayerScreen.Service.JetAudioService
import com.example.musicplayer.MusicPlayerScreen.Service.PlayerEvent
import com.example.musicplayer.MusicPlayerScreen.Service.PlayerEvent.*
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val audioDummy = AudioFile(
    id = -1,
    displayName = "No Audio",
    artist = "Unknown",
    album = "Unknown",
    duration = 0,
    albumIdForArt = -1
)
sealed interface uiToastMessage{
    data class meassage(val msg : String) : uiToastMessage
}

@OptIn(SavedStateHandleSaveableApi::class)
class MusicViewModel(
    private val audioService: AudioServiceHandler,
    private val repository: MusicRepository,
    private val playlist : PlaylistRepository,
    private val context: Context,
    saveStateHandler: SavedStateHandle
) : ViewModel() {
    var duration by saveStateHandler.saveable { mutableLongStateOf(0L) }
    var audioId by saveStateHandler.saveable { mutableStateOf(audioDummy.id.toInt()) }
    var progress by saveStateHandler.saveable { mutableFloatStateOf(0f) }
    var isPlaying by saveStateHandler.saveable { mutableStateOf(false) }
    var currentSelectedAudio by saveStateHandler.saveable { mutableStateOf(audioDummy) }
    var audioList by saveStateHandler.saveable { mutableStateOf(listOf<AudioFile>()) }
    var albumIdForArt by saveStateHandler.saveable { mutableStateOf("") }
    var playlistList by saveStateHandler.saveable { mutableStateOf(listOf<PlayList>()) }
    var repeatMode by saveStateHandler.saveable { mutableStateOf(value = Player.REPEAT_MODE_OFF) }
    var isShuffleEnabled by saveStateHandler.saveable { mutableStateOf(false) }



    private val _uiEffect = MutableSharedFlow<uiToastMessage>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.insertMediaStoreToDB()
        }
        loadAudioData()
        loadPlaylistData()
        startService()
        viewModelScope.launch {
            audioService.playerState.collectLatest { state ->
                when (state) {
                    is AudioState.Buffering -> calculateProgress(state.progress)
                    is AudioState.CurrentPlaying -> {
                        val song = audioList.find { it.id.toString() == state.mediaItems?.mediaId }
                        if (song != null) {
                            currentSelectedAudio = song
                        }
                    }
                    AudioState.Initial -> _uiState.value = UiState.Initial
                    is AudioState.Playing -> {
                        isPlaying = state.isPlaying
                    }
                    is AudioState.Progress -> {
                        calculateProgress(state.progress)
                    }
                    is AudioState.Ready -> {
                        duration = state.duration
                        _uiState.value = UiState.Ready
                    }

                    is AudioState.RepeatModeChanged -> {
                        repeatMode = state.repeatModeChanged
                    }

                    is AudioState.ShuffleModeChanged -> {
                        isShuffleEnabled = state.isShuffleEnabled
                    }
                }
            }
        }
    }

    fun onEvent(event: MusicEvent) {
        viewModelScope.launch {
            when (event) {
                MusicEvent.Backward -> audioService.onPlayerEvents(Backward)
                MusicEvent.Forward -> audioService.onPlayerEvents(Forward)
                MusicEvent.SeekToNext -> audioService.onPlayerEvents(SeekToNext)
                MusicEvent.SeekToPrevious -> audioService.onPlayerEvents(SeekToPrevious)
                MusicEvent.PlayPause -> audioService.onPlayerEvents(PlayPause)
                MusicEvent.Stop -> audioService.onPlayerEvents(Stop)


                is MusicEvent.SeekTo -> {
                    audioService.onPlayerEvents(
                        SeekTo,
                        seekPosition = (duration * event.seekto).toLong()
                    )
                }

                is MusicEvent.SelectedAudioChange -> {
                    audioService.onPlayerEvents(
                        SelectedAudioChange,
                        selectedAudioIndex = event.index
                    )
                }

                is MusicEvent.UpdateProgress -> {
                    audioService.onPlayerEvents(
                        UpdateProgress(event.newProgress)
                    )
                    progress = event.newProgress
                }

                is MusicEvent.LongUpdateProgress -> {
                    audioService.onPlayerEvents(
                        SeekTo,
                        seekPosition = event.newProgress
                    )
                }

                is MusicEvent.SelectedAudioIndex -> {
                    if (event.index in audioList.indices){
                        val song = audioList[event.index]
                        this@MusicViewModel.audioId = song.id.toInt()
                        this@MusicViewModel.currentSelectedAudio = song
                        audioService.onPlayerEvents(SelectedAudioChange , selectedAudioIndex = event.index)
                    }
                }

                is MusicEvent.PlayPlaylist -> {
                    playPlaylist(event.playlist)
                }

                is MusicEvent.ShufflePlaylist -> {
                    shufflePlaylist(event.playlist)
                }

                is MusicEvent.PlayOnlySong -> {
                    val song = event.audio
                    // --- Setting the ID for persistences ---
                    this@MusicViewModel.audioId = song.id.toInt()
                    this@MusicViewModel.currentSelectedAudio = song
                    onlyOnMediaItem(audio = event.audio.toMediaItem())
                }

                MusicEvent.ToggleRepeat -> {
                    audioService.onPlayerEvents(ToggleRepeat)
                    val message = when (repeatMode) {
                        Player.REPEAT_MODE_OFF -> "Repeat One"
                        Player.REPEAT_MODE_ONE -> "Repeat All"
                        else -> "Repeat Off"
                    }
                    _uiEffect.emit(
                        value = uiToastMessage.meassage(message)
                    )
                }

                MusicEvent.ToggleShuffle -> {
                    audioService.onPlayerEvents(ToggleShuffle)
                    _uiEffect.emit(
                        value = uiToastMessage.meassage(if (isShuffleEnabled) "Shuffle off" else "Shuffle on")
                    )
                }
            }
        }
    }

    private fun playPlaylist(id : Long ){
        viewModelScope.launch {
            playlist.getSongsInPlaylist(id).collect{
                this@MusicViewModel.audioList = it
                val mediaItems = it.map { audioFile -> audioFile.toMediaItem() }
                Log.d("mediaItems" , mediaItems.toString())
                setMediaItems(
                    mediaItems,
                    playWhenReady = true,
                )
            }
        }
    }

    private fun shufflePlaylist(id: Long) {
        viewModelScope.launch {
            playlist.getSongsInPlaylist(id).collect {
                val shuffledList = it.shuffled()
                this@MusicViewModel.audioList = shuffledList
                val mediaItems = shuffledList.map { audioFile -> audioFile.toMediaItem() }
                setMediaItems(
                    mediaItems,
                    playWhenReady = true,
                )
            }
        }
    }

    private fun calculateProgress(currentProgress: Long) {
        progress = if (currentProgress > 0 && duration > 0) {
            (currentProgress.toFloat() / duration.toFloat())
        } else 0f
    }

    private fun loadAudioData() {
        viewModelScope.launch {
            repository.getAllAudioFilesFromDb().collectLatest { audioFiles ->
                this@MusicViewModel.audioList = audioFiles
                
                // Restore selection based on saved audioId
                audioFiles.find { it.id.toInt() == audioId }?.let {
                    currentSelectedAudio = it
                }

                val mediaItems = audioFiles.map { it.toMediaItem() }
                setMediaItems(mediaItems , playWhenReady = false)
            }
        }
    }

    private fun loadPlaylistData() {
        viewModelScope.launch {
            playlist.getAllPlayList().collectLatest { playlists ->
                this@MusicViewModel.playlistList = playlists
            }
        }
    }

    private fun setMediaItems(audioList: List<MediaItem> ,playWhenReady: Boolean = false) {
        audioService.setMediaItems(
            mediaItems = audioList ,
            playWhenReady = playWhenReady

        )
    }
    private fun onlyOnMediaItem(audio : MediaItem){
        audioService.onPlayerEvents(OnAudioSongPlay(audio))
    }

    private fun startService() {
        val intent = Intent(context, JetAudioService::class.java)
        context.startService(intent)
    }
}

sealed class MusicEvent {

    data class PlayPlaylist(val playlist : Long) : MusicEvent()
    data class ShufflePlaylist(val playlist: Long) : MusicEvent()
    data class PlayOnlySong(val audio: AudioFile) : MusicEvent()
    object ToggleRepeat : MusicEvent()
    object ToggleShuffle : MusicEvent()
    object PlayPause : MusicEvent()
    data class SelectedAudioChange(val index: Int) : MusicEvent()
    object Backward : MusicEvent()
    object SeekToNext : MusicEvent()
    object SeekToPrevious : MusicEvent()
    object Forward : MusicEvent()
    data class UpdateProgress(val newProgress: Float) : MusicEvent()
    data class LongUpdateProgress(val newProgress: Long) : MusicEvent()
    object Stop : MusicEvent()
    data class SeekTo(val seekto: Float) : MusicEvent()
    data class SelectedAudioIndex(val index: Int) : MusicEvent()
}

sealed class UiState {
    object Initial : UiState()
    object Ready : UiState()
}
