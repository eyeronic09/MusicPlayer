package com.example.musicplayer.HomeScreen.data.local.converter

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverters

class converter {
    @TypeConverters
    fun toURI(uri : Uri?): String {
        return uri.toString()
    }
    @TypeConverters
    fun toString(str : String?) : Uri? {
        return str?.toUri()
    }
}