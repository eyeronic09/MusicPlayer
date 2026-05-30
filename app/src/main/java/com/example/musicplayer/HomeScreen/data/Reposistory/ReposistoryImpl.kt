package com.example.musicplayer.HomeScreen.data.Reposistory

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.HomeScreen.domain.model.AudioFile
import com.example.musicplayer.HomeScreen.domain.reposistory.MusicRepository

class ReposistoryImpl(
    private val context: Context
) : MusicRepository {
    override fun getAudioFiles(): List<AudioFile> {
        Log.d("MusicRepository", "Fetching audio files...")
        val audioFiles = mutableListOf<AudioFile>()
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ALBUM
        )
        val queryUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        try {
            context.contentResolver.query(
                queryUri,
                projection,
                null,
                null,
                "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getInt(durationColumn)
                    val album = cursor.getString(albumColumn)
                    audioFiles.add(
                        AudioFile(
                            id = id,
                            displayName = name,
                            artist = artist,
                            album = album,
                            duration = duration
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("MusicRepository", "Error querying MediaStore", e)
        }
        Log.d("MusicRepository", "Found ${audioFiles.size} audio files")
        return audioFiles
    }
}
