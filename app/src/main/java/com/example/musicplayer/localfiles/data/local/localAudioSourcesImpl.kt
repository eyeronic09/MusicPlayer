package com.example.musicplayer.localfiles.data.local

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.example.musicplayer.localfiles.domain.Model.Song

class localAudioSourcesImpl(private val context: Context) : localAudioDataSources {
    override fun fetchlocalAudioFiles(context: Context): List<Song> {
        val AllSongAudio = mutableListOf<Song>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        context.contentResolver.query(collection, projection, selection, null, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            Log.d("contentResolver" , " id = ${idColumn}" +
                    "Title  ${titleColumn}" +
                    "Artist  ${artistColumn}" +
                    "Data  ${dataColumn}"
            )

            while (cursor.moveToNext()){
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val duration = cursor.getString(durationColumn)
                val data = cursor.getString(dataColumn)

                AllSongAudio.add(
                    Song(
                        id = id,
                        title = title,
                        artist = artist,
                        duration = duration,
                        contentUri = data
                    )
                )
            }
        }

        return AllSongAudio

    }
}