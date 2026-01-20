package com.example.musicplayer.HomeScreen.Data_layer.local.mapper

import androidx.core.net.toUri
import com.example.musicplayer.HomeScreen.Data_layer.local.Entity.FolderEntity
import com.example.musicplayer.HomeScreen.Domain_layer.model.Folder

fun FolderEntity.toDomain(): Folder {
    return (
        Folder(
           id = this.id,
           folderUri = this.folderUri.toString(),
           folderName = this.folderName
        )
    )

}

fun Folder.toEntity(): FolderEntity {
    return (
        FolderEntity(
            id = this.id,
            folderUri = this.folderUri.toUri(),
            folderName = this.folderName
        )
    )
}