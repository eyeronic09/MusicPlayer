package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.Core_Screen.Screen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.FolderScreen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.ListScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicPlayerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Folder_Screen.route
                ) {
                    composable(route = Screen.Folder_Screen.route) {
                        FolderScreen(
                            onNavigateToList = { uri ->
                                navController.navigate(Screen.List_Screen.passUri(uri))
                            }
                        )
                    }
                    composable(
                        route = Screen.List_Screen.route,
                        arguments = listOf(
                            navArgument("uri") {
                                type = NavType.StringType
                            }
                        )
                    ) { backStackEntry ->
                        val uri = backStackEntry.arguments?.getString("uri") ?: ""
                        ListScreen(uri = uri)
                    }
                }
            }
        }
    }
}
