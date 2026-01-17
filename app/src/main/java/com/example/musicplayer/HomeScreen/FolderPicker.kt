package com.example.musicplayer.HomeScreen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PickAudioFolder(
    viewModel : SongPlayerVM = viewModel()
) {
    val context = LocalContext.current
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { result ->
        result ?: return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            result, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
        viewModel.PlayMusic(result)

    }
    Button(onClick = {folderPickerLauncher.launch(null) }  ) {
        Text("Use this Folder")
    }




}
