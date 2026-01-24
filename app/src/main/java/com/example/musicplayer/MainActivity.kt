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
import com.example.musicplayer.PlayerScreen.PlayerScreen
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
                            },
                            onNavigateToPlayer = { folderId ->
                                navController.navigate(Screen.PlayerScreen.createRoute(folderId))
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
                    composable(
                        route = Screen.PlayerScreen.route,
                        arguments = listOf(
                            navArgument("folderId") {
                                type = NavType.LongType
                            }
                        )
                    ){ backStackEntry ->
                        val folderId = backStackEntry.arguments?.getLong("folderId") ?: 1
                        PlayerScreen(navController = navController, folderId = folderId)

                    }
                }
            }
        }
    }
}
