package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.Core_Screen.Screen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.FolderScreen
import com.example.musicplayer.FolderScreen.Ui_Screen.FolderScreen.ListScreen
import com.example.musicplayer.PlayerScreen.PlayerScreen
import com.example.musicplayer.SettingsScreen.SettingsScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme

class MainActivity : ComponentActivity() {

    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MusicPlayerTheme {
                MainScreen(exoPlayer = exoPlayer)
            }
        }
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }
}

@Composable
fun MainScreen(exoPlayer: ExoPlayer) {
    val navController = rememberNavController()
    
    val items = listOf(
        Triple(Screen.Folder_Screen.route, "Library", Icons.Default.LibraryMusic),
        Triple(Screen.PlayerTab.route, "Player", Icons.Default.PlayArrow),
        Triple(Screen.SettingsTab.route, "Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar() {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = currentDestination?.hierarchy?.any { it.route == route } == true,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Folder_Screen.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Folder_Screen.route) {
                FolderScreen(
                    onNavigateToList = { uri ->
                        navController.navigate(Screen.List_Screen.passUri(uri))
                    },
                    onNavigateToPlayer = { folderId ->
                        // Navigate to Player Tab with folderId
                        navController.navigate(Screen.PlayerScreen.createRoute(folderId))
                    }
                )
            }

            composable(
                Screen.List_Screen.route,
                arguments = listOf(navArgument("uri") { type = NavType.StringType })
            ) { backStackEntry ->
                val uri = backStackEntry.arguments?.getString("uri") ?: ""
                ListScreen(
                    uri = uri,
                    onNavigateToPlayer = { audioUri ->
                        navController.navigate(Screen.SpecificAudioPlayer.createRoute(audioUri))
                    }
                )
            }
            composable(Screen.PlayerTab.route) {
                PlayerScreen(
                    navController = navController,
                    exoPlayer = exoPlayer
                )
            }
            composable(
                Screen.PlayerScreen.route,
                arguments = listOf(navArgument("folderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val folderId = backStackEntry.arguments?.getLong("folderId") ?: 0L
                PlayerScreen(
                    navController = navController,
                    folderId = folderId,
                    exoPlayer = exoPlayer
                )
            }

            composable(
                Screen.SpecificAudioPlayer.route,
                arguments = listOf(navArgument("audioUri") { type = NavType.StringType })
            ) { backStackEntry ->
                val audioUri = backStackEntry.arguments?.getString("audioUri") ?: ""
                PlayerScreen(
                    navController = navController,
                    audioUri = audioUri,
                    exoPlayer = exoPlayer
                )
            }

            composable(Screen.SettingsTab.route) {
                SettingsScreen()
            }
        }
    }
}
