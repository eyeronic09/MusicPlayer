package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.HorizontalFolderBar
import io.github.vinceglb.filekit.dialogs.compose.rememberDirectoryPickerLauncher
import org.koin.androidx.compose.koinViewModel

class FolderScreen : Screen {
    @Composable
    override fun Content() {

        val viewModel = koinViewModel<FolderScreenVM>()
        val state = viewModel.screenUiState.collectAsStateWithLifecycle()
        val event = viewModel::onEvent


        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree()
        ) { uri ->
            // This callback runs when the user selects a folder
            uri?.let {
                viewModel.onEvent(FolderScreenUiEvent.AddFolder(it))
            }
        }


        Scaffold(
            bottomBar = {
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        launcher.launch(null)
                    },
                    content = {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "Add Folder"
                        )
                    }
                )
            }

        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.padding(paddingValues)){
                    items(items = state.value.folders){ index ->
                        HorizontalFolderBar(folder = index)
                    }
                }
            }
        }
        }

}


@Preview(showSystemUi = true)
@Composable
private fun FolderPreview() {
    FolderPreview()


}

