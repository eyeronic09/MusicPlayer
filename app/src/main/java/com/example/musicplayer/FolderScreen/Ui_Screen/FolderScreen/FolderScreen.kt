package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.HorizontalFolderBar
import org.koin.androidx.compose.koinViewModel


@Composable
fun FolderScreen(
    viewModel: FolderScreenVM = koinViewModel(),
    onNavigateToList: (String) -> Unit,
    onNavigateToPlayer : (Long) -> Unit
) {
    val state by viewModel.screenUiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(FolderScreenUiEvent.AddFolder(it))
        }
    }

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
                            onNavigateToList(folder.folderUri)
                        },
                        onClickPlay = {
                            onNavigateToPlayer(folder.id.toLong())
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ListScreen(
    uri: String,
    viewModel: FolderScreenVM = koinViewModel(),
){
    val context = LocalContext.current
    val files = viewModel.listFile(context, uri.toUri())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Files in: $uri")
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(files) { fileUri ->
                Text(
                    text = fileUri.lastPathSegment ?: "Unknown File",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun FolderScreenPreview() {
    ListScreen(uri = "")
}