package com.benmohammad.unote.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
class Note(
    @field:ColumnInfo(name = "type") var type: Int,
    @field:ColumnInfo(name = "index") var index: Int
) {
    @PrimaryKey(autoGenerate = true)
    var uId: Long = 0

    @ColumnInfo(name = "text")
    var text: String? = null

    @ColumnInfo(name = "fileName")
    var fileName: String? = null

    @ColumnInfo(name = "uri")
    var uri: String? = null

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "noteId")
    var noteId: String? = null

    @ColumnInfo(name = "creationDate")
    var creationDate: String? = null

    @ColumnInfo(name = "lastUpdated")
    var lastUpdated: String? = null

    companion object {
        const val TYPE_TEXT = 1
        const val TYPE_IMAGE = 2
    }



}