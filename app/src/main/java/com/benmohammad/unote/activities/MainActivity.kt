package com.benmohammad.unote.activities

import android.content.Context
import android.content.Intent
import android.media.MediaCodec
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.benmohammad.unote.App
import com.benmohammad.unote.R
import com.benmohammad.unote.adapter.MyAdapter
import com.benmohammad.unote.model.Note
import com.benmohammad.unote.model.db.AppDatabase
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_notes_home.*

class MainActivity : AppCompatActivity() {
    lateinit var adapter: MyAdapter
    lateinit var appDatabase: AppDatabase
    lateinit var notes: ArrayList<Note>
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_home)
        initVars()
        setAdapter()
        setClicks()
    }

    override fun onStart() {
        super.onStart()
        fetchNotesAndDisplay()
    }

    fun setClicks() {
        fab.setOnClickListener {
            val intent = Intent(this, EditorActivity::class.java)
            startActivity(intent)
        }
    }

    fun initVars() {
        notes = arrayListOf()
        context = this
        appDatabase = App.getAppDatabase(this)
    }

    fun setAdapter() {
        rv.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, notes)
        rv.adapter = adapter
    }

    fun fetchNotesAndDisplay() {
        val allNotes = appDatabase.notesDao().getFirstNoteFromUid()
        tvAddNote.visibility = View.GONE

        val obsSingle = object : SingleObserver<List<Note>> {
            override fun onSubscribe(d: Disposable) {

            }

            override fun onSuccess(t: List<Note>) {
                if(t.isNotEmpty()) {
                    notes.clear()
                    notes.addAll(t)
                    adapter.notifyDataSetChanged()
                } else {
                    tvAddNote.visibility = View.VISIBLE
                }
            }

            override fun onError(e: Throwable) {
                Log.e("MainActivity onError", e.message.toString())
            }
        }

        allNotes.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(obsSingle)
    }
}