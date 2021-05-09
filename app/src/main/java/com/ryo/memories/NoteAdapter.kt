package com.ryo.memories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_note.view.*

class NoteAdapter(
    val notes: MutableList<Note>
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_note,
                parent,
                false
            )
        )
    }

    fun addNote(note: Note) {
        notes.add(0, note)
        notifyItemInserted(0)
    }

    fun deleteNote(note: Note) {
        notes.remove(note)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val curNote = notes[position]
        holder.itemView.apply {
            tvNote.text = curNote.noteBody
            tvDate.text = curNote.date
        }
    }
}