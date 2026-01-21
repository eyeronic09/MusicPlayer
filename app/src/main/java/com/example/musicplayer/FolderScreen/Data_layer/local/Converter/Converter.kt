package com.example.musicplayer.FolderScreen.Data_layer.local.Converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class Converter {
    @TypeConverter
    fun toUri(uriString: String?): Uri? = uriString?.toUri()


    @TypeConverter
    fun uriToString(uri : Uri) : String {
        return uri.toString()
    }
}
