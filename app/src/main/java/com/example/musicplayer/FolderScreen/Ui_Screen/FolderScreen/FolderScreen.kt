package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.HorizontalFolderBar
import org.koin.androidx.compose.koinViewModel

class FolderScreen : Screen {

    @Composable
    override fun Content() {

        val viewModel = koinViewModel<FolderScreenVM>()
        val state = viewModel.screenUiState.collectAsStateWithLifecycle()

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(16.dp)){
                items(items = state.value.folders){ index ->
                    HorizontalFolderBar(folder = index)
                }
            }
        }

    }

}


@Preview
@Composable
private fun FolderPreview() {
    FolderPreview()


}

