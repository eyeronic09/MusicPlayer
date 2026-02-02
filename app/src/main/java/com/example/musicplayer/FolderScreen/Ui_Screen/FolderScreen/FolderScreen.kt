package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.HorizontalFolderBar
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component.ListScreen
import org.koin.androidx.compose.koinViewModel


@Composable
fun FolderScreen(
    viewModel: FolderScreenVM = koinViewModel(),
    onNavigateToList: (String) -> Unit,
    onNavigateToPlayer : (Long) -> Unit
) {
    val state by viewModel.screenUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            // Take persistent permission so we can access the folder later
            val contentResolver = context.contentResolver
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)
            
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
    onNavigateToPlayer: (String) -> Unit,
    folderScreenVM: FolderScreenVM = koinViewModel(),
    context: Context = LocalContext.current
) {
    val files = folderScreenVM.listFile(context, uri.toUri())

    ListScreen(
        uri = uri.toUri(),
        files = files,
        onItemClick = { fileUri ->
            Log.d("FolderScreen", "File clicked: $fileUri")
            onNavigateToPlayer(fileUri.toString())
        }
    )
}