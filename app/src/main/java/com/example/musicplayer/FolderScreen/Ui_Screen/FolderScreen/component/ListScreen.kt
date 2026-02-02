package com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.component

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ListScreen(
    uri: Uri,
    files: List<Uri>,
    onItemClick: (Uri) -> Unit = {}
) {
    Scaffold (
        modifier = Modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = uri.lastPathSegment.toString()) }
            )
        }
    ){
        ListScreenContentContent(files, onItemClick)
    }
}

@Composable
private fun ListScreenContentContent(
    files: List<Uri>,
    onItemClick: (Uri) -> Unit = {}
) {

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .padding(top = 16.dp)
    ) {
        items(files) { fileUri ->
            val fileName = remember(fileUri)
            {
                try {
                    DocumentFile.fromSingleUri(context, fileUri)?.name ?: "Error getting file name"
                } catch (e: Exception) {
                    Log.e("ListScreen", "Error getting file name", e)
                }
            }
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onItemClick(fileUri) }
            ) {
                Text(modifier = Modifier.padding(16.dp), text = fileName.toString())
            }

        }
    }
}