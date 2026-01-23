package com.example.musicplayer.Core_Screen

import android.net.Uri

sealed class Screen(val route: String) {
    object Folder_Screen : Screen("Folder")
    object List_Screen : Screen("List/{uri}") {
        fun passUri(uri: String): String = "List/${Uri.encode(uri)}"
    }
    object PlayerScreen : Screen("PlayerScreen")
}
