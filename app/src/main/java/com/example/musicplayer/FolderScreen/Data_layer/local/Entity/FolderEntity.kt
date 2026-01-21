package com.example.musicplayer.FolderScreen.Data_layer.local.Entity

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Audio_File")
data class FolderEntity (
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val folderUri : Uri,
    val folderName : String
)