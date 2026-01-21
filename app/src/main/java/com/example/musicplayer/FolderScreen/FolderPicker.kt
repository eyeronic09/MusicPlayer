package com.example.musicplayer.FolderScreen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * A composable function that provides a UI component (Button) to select a directory from the system.
 *
 * This function launches the system's directory picker, requests persistable URI permissions
 * for the selected folder to ensure long-term access, and updates the [SongPlayerVM]
 * to begin processing or playing music from the chosen location.
 *
 * @param viewModel The [SongPlayerVM] instance used to handle the music logic once a folder is selected.
 */
@Composable
fun PickAudioFolder() {
    val context = LocalContext.current
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { result ->
        result ?: return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            result, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

    }
    Button(onClick = {folderPickerLauncher.launch(null) }  ) {
        Text("Use this Folder everytime")
    }




}
