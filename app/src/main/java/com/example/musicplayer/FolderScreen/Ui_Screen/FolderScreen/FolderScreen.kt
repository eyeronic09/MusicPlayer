package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable()
fun FolderScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp)
        ,
    ) {
        LazyColumn() {

        }
    }
}

@Preview
@Composable
private fun FolderPreview() {
    FolderPreview()
}