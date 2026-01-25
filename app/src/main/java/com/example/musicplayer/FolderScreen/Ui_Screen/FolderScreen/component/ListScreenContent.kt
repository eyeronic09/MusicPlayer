package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun ListScreenContent(
    uri: String,
    files: List<Uri>,
    onItemClick: (Uri) -> Unit = {}

) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()

    ) {
        Text(text = "Files in: $uri")
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            items(files) { fileUri ->
                Text(
                    text = fileUri.lastPathSegment ?: "Unknown File",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable { onItemClick(fileUri) }

                ).also {
                    Log.d("ListScreenContent", "${fileUri.lastPathSegment}")
                }


            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ListScreenPreview() {
    ListScreenContent(uri = "mock://folder", files = emptyList())
}