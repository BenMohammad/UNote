package com.benmohammad.unote.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.benmohammad.unote.model.Note
import io.reactivex.Single

@Dao
interface UNotesDao {

    @Query("select * from notes")
    fun getAllNotes(): Single<List<Note>>

    @Query("select * from notes where noteId = :noteId")
    fun getNotesForUid(noteId: String): Single<List<Note>>

    @Query("select * from notes where uId in(select min(uId) from notes group by noteId)")
    fun getFirstNoteFromUid(): Single<List<Note>>

    @Query("select * from notes where uId = :uId")
    fun getNotesForUid(uId: Long): Single<Note>

    @Insert
    fun insertAll(list: ArrayList<Note>)

    @Query("delete from notes where uId =:uId")
    fun deleteNotes(uId: Long)

    @Update
    fun updateNote(list: ArrayList<Note>): Int
}