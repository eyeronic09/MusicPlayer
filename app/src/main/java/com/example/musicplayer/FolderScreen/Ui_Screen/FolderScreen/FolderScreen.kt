package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.HorizontalFolderBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun FolderScreen(
    viewModel: FolderScreenVM = koinViewModel()
) {
    val state by viewModel.screenUiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(FolderScreenUiEvent.AddFolder(it))
        }
    }

    if (state.isFolderClick) {
        BackHandler {
            viewModel.onEvent(FolderScreenUiEvent.NavigateBack)
        }
        ListScreen()
    } else {
        Scaffold(
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    items(items = state.folders) { folder ->
                        HorizontalFolderBar(
                            folder = folder,
                            onClickList = {
                                viewModel.onEvent(FolderScreenUiEvent.SelectedUriToList(folder.folderUri))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ListScreen(
    viewModel: FolderScreenVM = koinViewModel(),
){
    val state by viewModel.screenUiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Files in: ${state.selectedUriToList}")
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            // Add items here later
        }
    }
}
