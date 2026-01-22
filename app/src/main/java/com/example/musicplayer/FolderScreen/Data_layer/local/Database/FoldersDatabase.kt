package com.example.musicplayer.FolderScreen.Data_layer.local.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.musicplayer.FolderScreen.Data_layer.local.Converter.Converter
import com.example.musicplayer.FolderScreen.Data_layer.local.Dao.FolderDao
import com.example.musicplayer.FolderScreen.Data_layer.local.Entity.FolderEntity

@TypeConverters(Converter::class)
@Database(
    entities = [FolderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FoldersDatabase() : RoomDatabase() {
    abstract val dao : FolderDao

    companion object {
        @Volatile
        private var INSTANCE : FoldersDatabase ? = null

        fun getIntance(context: Context) :  FoldersDatabase {
            return INSTANCE?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = FoldersDatabase::class.java,
                    name = "FolderDatabase"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}