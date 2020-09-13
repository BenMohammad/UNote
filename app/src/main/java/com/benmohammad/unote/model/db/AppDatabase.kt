package com.benmohammad.unote.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.benmohammad.unote.model.Note

@Database(entities = [ Note::class], version = 1, exportSchema = true)
abstract class AppDatabase: RoomDatabase(){

    abstract fun notesDao(): UNotesDao
}