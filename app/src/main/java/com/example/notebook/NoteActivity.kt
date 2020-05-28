package com.example.notebook

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_page.*

class NoteActivity : AppCompatActivity() {


    val notedataset: MutableList<String> = arrayListOf()

    val context = this
    var db = DatabaseHandler(context)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_page)
        var notetitle = intent.getStringExtra("key") //Title
        setTitle(notetitle)

        val note = db.readNoteData(notetitle)
        val notetext = findViewById<EditText>(R.id.noteET)
        notetext.setText(note)

        cameraFB.setOnClickListener {
            val intent = Intent(cameraFB.context, CameraActivity::class.java)
            cameraFB.context.startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menu_inflater: MenuInflater = menuInflater
        menu_inflater.inflate(R.menu.note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.save_icon){
            Toast.makeText(this,"Save Clicked", Toast.LENGTH_LONG).show()

            val notecontent = noteET.text.toString()
            var user = User("",notecontent)
            val notetitle = intent.getStringExtra("key")
            db.insertNoteData(user, notetitle)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}