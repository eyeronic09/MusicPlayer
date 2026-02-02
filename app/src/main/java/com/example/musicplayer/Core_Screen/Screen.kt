package com.example.musicplayer.Core_Screen

import android.net.Uri

sealed class Screen(val route: String) {
    object Folder_Screen : Screen("Folder")
    object List_Screen : Screen("List/{uri}") {
        fun passUri(uri: String): String = "List/${Uri.encode(uri)}"
    }
    
    object PlayerTab : Screen("player_tab")
    object LibraryTab : Screen("library_tab")
    object SettingsTab : Screen("settings_tab")

    object PlayerScreen : Screen("player/{folderId}") {
        fun createRoute(folderId: Long): String {
            return "player/$folderId"
        }
    }
    
    object SpecificAudioPlayer : Screen("specific_audio/{audioUri}") {
        fun createRoute(audioUri: String): String {
            return "specific_audio/${Uri.encode(audioUri)}"
        }
    }
}
