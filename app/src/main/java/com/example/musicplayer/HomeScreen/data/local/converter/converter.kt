package com.example.musicplayer.HomeScreen.data.local.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromUri(uri : Uri?): String? {
        return uri?.toString()
    }
    @TypeConverter
    fun toUri(str : String?) : Uri? {
        return str?.toUri()
    }
}