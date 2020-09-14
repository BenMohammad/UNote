package com.benmohammad.unote.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.benmohammad.unote.R
import com.benmohammad.unote.model.FileModal
import com.benmohammad.unote.model.MediaMetaData
import com.benmohammad.unote.model.Note
import com.bumptech.glide.Glide
import java.io.File

class EditorView: FrameLayout {

    private var mContext: Context
    lateinit var ll: LinearLayout
    val etList = arrayListOf<AppCompatEditText>()
    val imgList = arrayListOf<AppCompatImageView>()
    val notesList = arrayListOf<Note>()
    val uriList = arrayListOf<MediaMetaData>()

    fun showNotesFromDB(notes: List<Note>) {
        clearAllList()
        notesList.addAll(notes)

        notesList.forEach{x ->
            when(x.type) {
                Note.TYPE_TEXT -> addEditTextFromDB(x)
                Note.TYPE_IMAGE -> addImageFromDB(x)
            }
        }
        addLastEditText()
    }

    private fun clearAllList() {
        etList.clear()
        imgList.clear()
        notesList.clear()
        ll.removeAllViews()
    }

    private fun addLastEditText() {
        if(isLastEtFocused()) {
            addNewEditText()
        }
    }

    private fun addEditTextFromDB(note: Note) {
        val et = AppCompatEditText(context)
        et.layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.addView(et)
        et.gravity = Gravity.TOP
        et.setText(note.text)

        if(etList.size > 1)
            etList.get(etList.size - 1).clearFocus()

        et.requestFocus()
        etList.add(et)
    }

    private fun addImageFromDB(note: Note) {
        val filePath = note.fileName
        val file = File(context.filesDir, filePath)
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        addBitmapOnly(bitmap)
    }

    private fun addBitmapOnly(bitmap: Bitmap) {
        val imageView = AppCompatImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = false
        ll.addView(imageView)
        imageView.setImageBitmap(bitmap)
    }

    public fun addBitmap(uri: Uri, bitmap: Bitmap, extension: String, shouldUpdateNoteList: Boolean = true) {
        val imageView = AppCompatImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = false
        var focusedEt = 0
        var size: Int = ll.childCount
        val indexes = 0..size

        for(i in indexes) {
            if(ll.getChildAt(i) is AppCompatEditText) {
                ll.getChildAt(i).layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                if(ll.getChildAt(i).isFocused) {
                    focusedEt = i
                }
            }
        }
        val indexOfImageVIew = focusedEt + 1
        ll.addView(imageView, indexOfImageVIew)
        imageView.setImageBitmap(bitmap)
        imgList.add(imageView)

        val index = ll.childCount
        val note = Note(index, Note.TYPE_IMAGE)
        uriList.add(MediaMetaData(uri, extension))
        note.uri = uri.toString()
        if(shouldUpdateNoteList) {
            notesList.add(note)

            if(isLastEtFocused()) {
                addNewEditText()
            }
        }
    }

    private fun isLastEtFocused(): Boolean {
        return etList.get(etList.size -1).isFocused
    }

    private fun addNewEditText(shouldUpdateNoteList: Boolean = true) {
        val et = AppCompatEditText(context)
        et.layoutParams = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        ll.addView(et)
        et.gravity = Gravity.TOP
        et.setHint(etList.size.toString())

        if(etList.size > 1)
            etList.get(etList.size -1).clearFocus()

        et.requestFocus()
        etList.add(et)
        val index = ll.childCount
        notesList.add(Note(index, Note.TYPE_TEXT))
    }

    fun addImage(fileModal: FileModal) {
        val resultUri = fileModal.uri
        val absolutePath = resultUri?.path

        val imageView = AppCompatImageView(context)
        imageView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        imageView.adjustViewBounds = true

        etList.forEach { x -> x.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT) }

        ll.addView(imageView)
        Glide.with(context)
            .load(absolutePath)
            .into(imageView)
    }




    constructor(context: Context): super(context) {
        this.mContext = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        this.mContext = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        this.mContext = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        this.mContext = context
        inflateLayout()
    }


    private fun inflateLayout() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_editor, this, true)
        val et_1: AppCompatEditText = view.findViewById(R.id.et_1)
        etList.add(et_1)
        ll = view.findViewById(R.id.ll)
        notesList.add(Note(Note.TYPE_TEXT, 0))
    }
}