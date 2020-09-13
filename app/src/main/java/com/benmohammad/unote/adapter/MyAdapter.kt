package com.benmohammad.unote.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.benmohammad.unote.R
import com.benmohammad.unote.activities.EditorActivity
import com.benmohammad.unote.model.Note


class MyAdapter(val context: Context, val notes: List<Note>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, null, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        val notes = this.notes[position]
        viewHolder.init(notes)
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var tv: AppCompatTextView = itemView.findViewById<View>(R.id.tv_title) as AppCompatTextView
        var notes: Note? = null
        fun init(note: Note) {
            this.notes = notes
            val title: String? = note.title
            val text: String? = note.text
            tv.text = text
            setClicks()
        }

        fun setClicks() {
            tv.setOnClickListener {v ->
                val intent = Intent(v.context, EditorActivity::class.java)
                intent.putExtra("uId", notes?.uId)
                intent.putExtra("noteId", notes?.noteId)
                v.context.startActivity(intent)
            }
        }

    }
}