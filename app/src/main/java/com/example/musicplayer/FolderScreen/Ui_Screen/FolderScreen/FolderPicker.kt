package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun filePicker(
    onFolderClicked :(uri : Uri) -> Unit,

){
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = {  uri ->
            if (uri != null){
                onFolderClicked( uri)
            }
        }
    )

}