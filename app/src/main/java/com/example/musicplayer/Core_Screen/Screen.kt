package com.example.musicplayer.Core_Screen

import android.net.Uri

sealed class Screen(val route: String) {
    object Folder_Screen : Screen("Folder")
    object List_Screen : Screen("List/{uri}") {
        fun passUri(uri: String): String = "List/${Uri.encode(uri)}"
    }
    // this is Folder id  can be your playlist which it will be used from repository
    object PlayerScreen : Screen("player/{folderId}") {

        fun createRoute(folderId: Long): String {
            return "player/$folderId"
        }
    }


}
