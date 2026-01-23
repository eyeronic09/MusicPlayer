package com.example.musicplayer.Core_Screen

import android.net.Uri

sealed class Screen(val route: String) {
    object Folder_Screen : Screen("Folder")
    object PlayerScreen : Screen("PlayerScreen")

}
