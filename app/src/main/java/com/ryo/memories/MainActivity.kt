package com.ryo.memories

import android.R.*
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat;
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //read the memories list from internal storage
        val fileName = "memories"
        var noteList = mutableListOf<Note>()

        //setup the json serializer and variables
        val gson = Gson()
        var jsonString: String

        //if 'memories' file already exists
        var files: Array<String> = this.fileList()
        if (files.contains(fileName)) {
            //retrieve the json string from internal storage
            var fileInputStream: FileInputStream? = null
            fileInputStream = openFileInput(fileName)
            var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            var text: String? = null
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
            val temp = stringBuilder.toString()

            //convert json string to a mutable list
            val itemType = object : TypeToken<List<Note>>() {}.type
            val itemList = gson.fromJson<List<Note>>(temp, itemType)
            noteList = itemList.toMutableList()
        }

        noteAdapter = NoteAdapter(noteList)

        rvNoteList.adapter = noteAdapter
        rvNoteList.layoutManager = LinearLayoutManager(this)

        fabAddNote.setOnClickListener {
            //builds the alert dialog that will serve as the input
            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(
                ContextThemeWrapper(this, R.style.AlertDialogCustom)
            )
            builder.setTitle("Memory")

            val viewInflated: View = LayoutInflater.from(this)
                .inflate(R.layout.note_input,
                    findViewById<ViewGroup>(id.content),
                    false
                )

            builder.setView(viewInflated)

            builder.setPositiveButton(
                string.ok
            ) { dialog, which ->
                dialog.dismiss()
                //stores the input
                val mText = viewInflated.findViewById<EditText>(R.id.input).text.toString()

                //gets the current date of the system
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("MMM dd, yyyy, hh:mm aaa")
                val date = dateFormat.format(calendar.time)

                if (mText.isNotEmpty()) {
                    val note = Note(mText, date)
                    noteAdapter.addNote(note)

                    //convert the list of note objects into a json string
                    jsonString = gson.toJson(noteAdapter.notes)

                    //save json string to internal storage
                    openFileOutput(fileName, Context.MODE_PRIVATE).use {
                        it.write(jsonString.toByteArray())
                    }

                    //keep screen at the top of the recycler view when you add notes
                    (rvNoteList.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0);
                }
            }
            builder.setNegativeButton(
                string.cancel
            ) { dialog, which -> dialog.cancel() }

            builder.show()
        }

        val itemTouchHelperCallback =
        object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteAdapter.deleteNote(noteAdapter.notes[(viewHolder.bindingAdapterPosition)])

                //convert the list of note objects into a json string
                jsonString = gson.toJson(noteAdapter.notes)

                //save json string to internal storage
                openFileOutput(fileName, Context.MODE_PRIVATE).use {
                    it.write(jsonString.toByteArray())
                }

                //notifies user that a memory has been deleted
                Toast.makeText(
                    this@MainActivity,
                    "Memory Deleted",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvNoteList)
    }


}