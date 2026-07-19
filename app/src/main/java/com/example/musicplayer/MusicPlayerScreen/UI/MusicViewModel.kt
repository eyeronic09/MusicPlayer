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
import com.example.musicplayer.MusicPlayerScreen.Service.AudioServiceHandler
import com.example.musicplayer.MusicPlayerScreen.Service.AudioState
import com.example.musicplayer.MusicPlayerScreen.Service.JetAudioService
import com.example.musicplayer.MusicPlayerScreen.Service.PlayerEvent.*
import com.example.musicplayer.MusicPlayerScreen.mapper.toMediaItem
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.time.TimeInMillis

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

@OptIn(SavedStateHandleSaveableApi::class, DelicateCoroutinesApi::class)
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
    var displayName by saveStateHandler.saveable { mutableStateOf("") }
    var artist by saveStateHandler.saveable { mutableStateOf("") }



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
                        val song = audioList.find { it.id.toString() == state.mediaItem?.mediaId }
                        Log.d("currentPlaying" , song.toString())
                        if (song != null) {
                            updateSelectedAudioAsState(song)
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

                is MusicEvent.LongUpdateProgress -> {
                    audioService.onPlayerEvents(SeekTo(event.newProgress))
                }

                is MusicEvent.PlayOnlySong -> {
                    updateSelectedAudioAsState(event.audio)
                    audioService.onPlayerEvents(OnAudioSongPlay(event.audio.toMediaItem()))
                }

                MusicEvent.PlayPause -> audioService.onPlayerEvents(PlayPause)

                is MusicEvent.PlayPlaylist -> {
                    playPlaylist(event.playlist)
                }

                is MusicEvent.SeekTo -> {
                    audioService.onPlayerEvents(
                        SeekTo((duration * event.seekto).toLong())
                    )
                }

                MusicEvent.SeekToNext -> {
                    audioService.onPlayerEvents(SeekToNext)
                }

                MusicEvent.SeekToPrevious -> audioService.onPlayerEvents(SeekToPrevious)

                is MusicEvent.SelectedAudioChange -> {
                    this@MusicViewModel.audioList = event.audioList
                    updateSelectedAudioAsState(event.audio)
                    Log.d("currentPlaying" , "current Selected Audio $currentSelectedAudio   this is ${event.audio}")
                    audioService.onPlayerEvents(
                        SelectedAudioChange(
                            homeSongAudio = event.audio,
                            audioList = event.audioList
                        )
                    )
                }

                is MusicEvent.SelectedAudioIndex -> {
                    val audio = this@MusicViewModel.audioList.getOrNull(event.index)
                    if (audio != null) {
                        updateSelectedAudioAsState(audio)
                        audioService.onPlayerEvents(
                            SelectedAudioChange(
                                homeSongAudio = audio,
                                audioList = this@MusicViewModel.audioList
                            )
                        )
                    }
                }

                is MusicEvent.ShufflePlaylist -> {
                    shufflePlaylist(event.playlist)
                }

                MusicEvent.Stop -> audioService.onPlayerEvents(Stop)

                is MusicEvent.PlaythisNext -> {
                    val currentIndex = audioService.currentMediaItemIndex
                    audioService.onPlayerEvents(PlaythisNext(event.mediaItem.toMediaItem()))
                    Log.d("currentPlaying", "Play next: ${event.mediaItem.displayName} at index $currentIndex")
                }

                MusicEvent.ToggleRepeat -> {
                    audioService.onPlayerEvents(ToggleRepeat)
                }

                MusicEvent.ToggleShuffle -> {
                    audioService.onPlayerEvents(ToggleShuffle)
                }

                is MusicEvent.UpdateProgress -> {
                    progress = event.newProgress
                    audioService.onPlayerEvents(UpdateProgress(newProgress = event.newProgress))
                }

                is MusicEvent.SleepTimer -> {
                    startSleepTimer(event.millis)
                }
            }
        }
    }
    private fun startSleepTimer(durationMs: Int) {
        val intent = Intent(context, JetAudioService::class.java).apply {
            action = "START_SLEEP_TIMER"
            putExtra("TIMER_DURATION_MS", durationMs.toLong())
        }
        context.startService(intent)
        Log.d("TimerCheck", "Intent sent to JetAudioService with duration: $durationMs ms")

    }
    private fun playPlaylist(id : Long ){
        viewModelScope.launch {
            playlist.getSongsInPlaylist(id).collect{
                this@MusicViewModel.audioList = it
                val mediaItems = it.map { audioFile -> audioFile.toMediaItem() }
                Log.d("mediaItems" , mediaItems.toString())
                setMediaItems(
                    audioList = mediaItems,
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
        Log.d("player" , "state $currentProgress  and dur $duration")
        progress = if (duration > 0) {
            (currentProgress.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else 0f

    }

    private fun loadAudioData() {
        viewModelScope.launch {
            repository.getAllAudioFilesFromDb().collect { audioFiles ->
                this@MusicViewModel.audioList = audioFiles
                val currentMediaId = audioService.currentMediaItem?.mediaId
                if (currentMediaId != null) {
                    audioFiles.find { it.id.toString() == currentMediaId }?.let {
                        updateSelectedAudioAsState(it)
                    }
                } else {
                    audioFiles.find { it.id.toInt() == audioId }?.let {
                        updateSelectedAudioAsState(it)
                    }
                }
            }
        }
    }

    private fun updateSelectedAudioAsState(song: AudioFile) {
        currentSelectedAudio = song
        audioId = song.id.toInt()
        duration = song.duration.toLong()
        displayName = song.displayName
        artist = song.artist
        albumIdForArt = song.albumIdForArt.toString()
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
    data class SleepTimer(val millis: Int) : MusicEvent()
    data class PlayPlaylist(val playlist : Long) : MusicEvent()
    data class ShufflePlaylist(val playlist: Long) : MusicEvent()
    data class PlayOnlySong(val audio: AudioFile) : MusicEvent()
    object ToggleRepeat : MusicEvent()
    object ToggleShuffle : MusicEvent()
    object PlayPause : MusicEvent()
    data class SelectedAudioChange(val audio : AudioFile , val audioList: List<AudioFile>) : MusicEvent()
    object Backward : MusicEvent()
    object SeekToNext : MusicEvent()
    object SeekToPrevious : MusicEvent()
    object Forward : MusicEvent()
    data class UpdateProgress(val newProgress: Float) : MusicEvent()
    data class LongUpdateProgress(val newProgress: Long) : MusicEvent()
    object Stop : MusicEvent()
    data class PlaythisNext(val index : Int = 0, val mediaItem: AudioFile) : MusicEvent()
    data class SeekTo(val seekto: Float) : MusicEvent()
    data class SelectedAudioIndex(val index: Int) : MusicEvent()
}

sealed class UiState {
    object Initial : UiState()
    object Ready : UiState()
}
