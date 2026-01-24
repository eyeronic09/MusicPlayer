package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.musicplayer.FolderScreen.Domain_layer.model.Folder

@Composable
fun HorizontalFolderBar(
    folder: Folder,
    onClickList : () -> Unit ,
    onClickPlay : () -> Unit
) {
    OutlinedCard(
        onClick = {
            onClickList()
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(all =16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Icon(
                modifier = Modifier.padding(13.dp),
                imageVector = Filled.FolderOpen,
                contentDescription = "Folder-Icon"
            )
            Text(text = folder.folderName)
            IconButton(
                onClick = onClickPlay,
                modifier = Modifier,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Folder"
                )
            }

        }






    }
}
@Preview
@Composable
private fun HorizontalPreview() {
    HorizontalFolderBar(
        folder = Folder(
            id = 1,
            folderName = "Mock Folder",
            folderUri = "mock://folder"
        ),
        onClickList = {},
        onClickPlay = {}
    )
}
