package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.FolderScreen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.FolderScreenVM
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                val VM : FolderScreenVM = koinViewModel()
                FolderScreen(VM)
            }
        }
    }
}
