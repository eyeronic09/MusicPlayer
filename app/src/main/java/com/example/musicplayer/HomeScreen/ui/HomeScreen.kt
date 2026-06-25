package com.example.musicplayer.HomeScreen.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.example.musicplayer.HomeScreen.Playlist.domain.model.PlayList
import com.example.musicplayer.HomeScreen.Playlist.ui.PlayListScreen
import com.example.musicplayer.HomeScreen.compontent.AudioItem
import com.example.musicplayer.HomeScreen.compontent.BottomBarPlayer
import com.example.musicplayer.HomeScreen.compontent.PlaylistBottomSheet
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.MusicPlayerScreen.UI.MusicEvent
import com.example.musicplayer.MusicPlayerScreen.UI.MusicViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.floor
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.HomeScreen.compontent.Searchingbar
import androidx.compose.runtime.collectAsState

enum class HomeTabs(val title: String) {
    Songs("Songs"),
    Album("Album"),
    Playlist("Playlist")
}

object HomeScreenTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val title = "Home"
            val icon = rememberVectorPainter(Icons.Default.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(
            HomeScreenRootScreen)
    }
}

object HomeScreenRootScreen : Screen {
    @Composable
    override fun Content() {
        HomeScreenRoot()
    }

}
@Composable
fun HomeScreenRoot(
    musicViewModel: MusicViewModel = koinViewModel(),
    homeViewModel: HomeScreenViewModel = koinViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        homeViewModel.uiEffect.collect {  effect ->
            when(effect){
                is HomeUiEffect.showToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    HomeScreen(
        progress = musicViewModel.progress,
        onProgress = { musicViewModel.onEvent(MusicEvent.UpdateProgress(it)) },
        isAudioPlaying = musicViewModel.isPlaying,
        currentPlayingAudio = musicViewModel.currentSelectedAudio,
        audiList = if (uiState.isSearching) uiState.filteredItem else uiState.allSongs,
        onStart = { musicViewModel.onEvent(MusicEvent.PlayPause) },
        onItemClick = { musicViewModel.onEvent(MusicEvent.SelectedAudioChange(it)) },
        onNext = { musicViewModel.onEvent(MusicEvent.SeekToNext) },
        onPrevious = { musicViewModel.onEvent(MusicEvent.SeekToPrevious) },
        playList = musicViewModel.playlistList,
        repeatMode = musicViewModel.repeatMode,
        isShuffleEnabled = musicViewModel.isShuffleEnabled,
        onAddToPlaylist = { audio, playlistId ->
            homeViewModel.onEvent(HomeEvent.AddToPlaylist(audio, playlistId))
        },
        onRepeat = {
            musicViewModel.onEvent(MusicEvent.ToggleRepeat)
        },
        onShuffle = {
            musicViewModel.onEvent(MusicEvent.ToggleShuffle)
        },
        onOpenSearch = { homeViewModel.onEvent(HomeEvent.OpenSearchBar) },
        isSearching = uiState.isSearching,
        searchQuery = uiState.searchedQuery,
        onSearchQueryChange = { homeViewModel.onEvent(HomeEvent.OnSearchQueryChange(it)) },
        onCloseSearch = { homeViewModel.onEvent(HomeEvent.CloseSearchBar) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    progress: Float,
    isSearching: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    onProgress: (Float) -> Unit,
    isAudioPlaying: Boolean,
    currentPlayingAudio: AudioFile,
    audiList: List<AudioFile>,
    playList: List<PlayList>,
    repeatMode: Int,
    isShuffleEnabled: Boolean,
    onStart: () -> Unit,
    onRepeat: () -> Unit,
    onShuffle: () -> Unit,
    onItemClick: (AudioFile) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onAddToPlaylist: (AudioFile, Long) -> Unit,
    onOpenSearch: () -> Unit
) {

    val tabs = HomeTabs.entries
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var selectedAudio by remember { mutableStateOf<AudioFile?>(null) }



    if (showBottomSheet) {
        PlaylistBottomSheet(
            playlists = playList,
            onPlaylistClick = { playlist ->
                selectedAudio?.let { audioId ->
                    onAddToPlaylist(audioId, playlist.playlistId)
                }
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        )
    }


    Scaffold(
        topBar = {
            Column {
                if (isSearching){
                    Searchingbar(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        onSearch = {
                        },
                        active = isSearching,
                        onActiveChange = { if (!it) onCloseSearch() },
                        content = {}
                    )
                }else {
                    TopAppBar(
                        title = { Text(text = "Music Player") },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                        actions = {
                            IconButton(onClick = onOpenSearch  ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }


                SecondaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(text = tab.title) }
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (currentPlayingAudio.id != -1L) {
                BottomBarPlayer(
                    progress = progress,
                    onProgress = onProgress,
                    audio = currentPlayingAudio,
                    onStart = onStart,
                    onNext = onNext,
                    isAudioPlaying = isAudioPlaying,
                    onPrevious = onPrevious,
                    onRepeat = onRepeat,
                    repeatMode = repeatMode,
                    isShuffleEnabled = isShuffleEnabled,
                    onShuffle = onShuffle
                )
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            when (tabs[page]) {
                HomeTabs.Songs -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        itemsIndexed(items = audiList) { index, audio ->
                            Log.d("HomeScreen", "Rendering index: $index, audio: ${audio.id}")
                            AudioItem(
                                audio = audio,
                                isSelected = { audio.id == currentPlayingAudio.id },
                                onItemClick = { onItemClick(audio) },
                                onAddToPlaylist = {
                                    selectedAudio = audio
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                HomeTabs.Album -> {
                    Text(text = "Album Screen")
                }
                HomeTabs.Playlist -> {
                    PlayListScreen().Content()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val mockAudioList = listOf(
        AudioFile(id = 1, albumIdForArt = 1, displayName = "Song One", artist = "Artist A", album = "Album 1", duration = 120000),
        AudioFile(id = 2, albumIdForArt = 2, displayName = "Song Two", artist = "Artist B", album = "Album 2", duration = 180000),
        AudioFile(id = 3, albumIdForArt = 3, displayName = "Song Three", artist = "Artist C", album = "Album 3", duration = 240000)
    )

    Surface {
        HomeScreen(
            progress = 0.5f,
            isSearching = true,
            searchQuery = "fdi",
            onSearchQueryChange = {},
            onCloseSearch = {},
            onProgress = {},
            isAudioPlaying = false,
            currentPlayingAudio = mockAudioList[0],
            audiList = mockAudioList,
            playList = emptyList(),
            repeatMode = 0,
            isShuffleEnabled = false,
            onStart = {},
            onRepeat = {},
            onShuffle = {},
            onItemClick = {},
            onNext = {},
            onPrevious = {},
            onAddToPlaylist = { _, _ -> },
            onOpenSearch = {}
        )
    }
}

 fun timeStampToDuration(position: Long): String {
    val totalSecond = floor(position / 1E3).toInt()
    val minutes = totalSecond / 60
    val remainingSeconds = totalSecond - (minutes * 60)
    return if (position < 0) "--:--"
    else "%d:%02d".format(minutes, remainingSeconds)
}
