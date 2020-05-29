package com.example.notebook

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor

class ViewActivity : AppCompatActivity() {

    val context = this
    var db = DatabaseHandler(context)

    override fun onCreate(savedInstanceState: Bundle?) {

        val markwon = Markwon.create(context)
        val editor = MarkwonEditor.create(markwon)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewing_page)
        val notetitle = intent.getStringExtra("key") //Title
        setTitle(notetitle)
        val note = db.readNoteData(notetitle)
        val viewNoteText = findViewById<TextView>(R.id.tv_note)
        println("PRINT NOTE DATA " + note)
        viewNoteText.setText(note)
        markwon.setMarkdown(viewNoteText,note)


        viewNoteText.setOnClickListener{
            val intent = Intent(this, NoteActivity::class.java)
            //val intent = Intent(v.context, NoteActivity::class.java)
            intent.putExtra("key", notetitle)
            this.startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        val markwon = Markwon.create(context)
        val editor = MarkwonEditor.create(markwon)

        setContentView(R.layout.viewing_page)
        val notetitle = intent.getStringExtra("key") //Title
        setTitle(notetitle)
        val note = db.readNoteData(notetitle)
        val viewNoteText = findViewById<TextView>(R.id.tv_note)
        viewNoteText.setText(note)
        markwon.setMarkdown(viewNoteText,note)


        viewNoteText.setOnClickListener{
            val intent = Intent(this, NoteActivity::class.java)
            //val intent = Intent(v.context, NoteActivity::class.java)
            intent.putExtra("key", notetitle)
            this.startActivity(intent)
        }


    }
}