package com.example.notebook

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher
import kotlinx.android.synthetic.main.note_page.*
import org.commonmark.node.SoftLineBreak
import java.util.concurrent.Executors


class NoteActivity : AppCompatActivity() {

    val context = this
    var db = DatabaseHandler(context)




    override fun onCreate(savedInstanceState: Bundle?) {
        val markwon = Markwon.builder(context)
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    builder.on(
                        SoftLineBreak::class.java
                    ) { visitor, softLineBreak -> visitor.forceNewLine() }
                }
            })
            .build()
        val editor = MarkwonEditor.create(markwon)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_page)

        val notetitle = intent.getStringExtra("key") //Title
        setTitle(notetitle)
        val note = db.readNoteData(notetitle)
        println("Note when read " + note)
        val notetext = findViewById<EditText>(R.id.noteET)
        notetext.setText(note)
        notetext.addTextChangedListener(MarkwonEditorTextWatcher.withPreRender(
            editor,
            Executors.newCachedThreadPool(),
            notetext))



        cameraFB.setOnClickListener {
            val intent = Intent(cameraFB.context, CameraActivity::class.java)
            cameraFB.context.startActivity(intent)
        }

        boldBtn.setOnClickListener {
            val start: Int = notetext.getSelectionStart()
            notetext.text.insert(start,"**")
        }
        italicBtn.setOnClickListener {
            val start: Int = notetext.getSelectionStart()
            notetext.text.insert(start,"*")
        }
        listBtn.setOnClickListener {
            val start: Int = notetext.getSelectionStart()
            notetext.text.insert(start,"- ")

        }
        headingBtn.setOnClickListener {
            val start: Int = notetext.getSelectionStart()
            notetext.text.insert(start,"#")
        }


        }

        override fun onBackPressed(){
            //Function for save changes
            val saveChanges = {dialog:DialogInterface, which: Int->
                Toast.makeText(this,"Saved", Toast.LENGTH_LONG).show()
                val notecontent = noteET.text.toString()
                val user = User("",notecontent)
                val notetitle = intent.getStringExtra("key")
                db.insertNoteData(user, notetitle)
                super.onBackPressed()
            }

            val noSaveChanges = {dialog:DialogInterface, which: Int->
                super.onBackPressed()
                super.onBackPressed()
            }

            val notetitle = intent.getStringExtra("key")
            val note = db.readNoteData(notetitle)
            val notetext  = findViewById<EditText>(R.id.noteET)
            if (note != notetext.text.toString()){
                val changesBuilder = AlertDialog.Builder(this)
                changesBuilder.setTitle("Confirm changes")
                changesBuilder.setMessage("Save changes?")
                changesBuilder.setPositiveButton("YES",DialogInterface.OnClickListener(saveChanges))
                changesBuilder.setNegativeButton("NO",DialogInterface.OnClickListener(noSaveChanges))
                val changesDialog = changesBuilder.create()
                changesDialog.show()
            }
            else{
                super.onBackPressed()
                super.onBackPressed()
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
            Toast.makeText(this,"Note Saved", Toast.LENGTH_LONG).show()

            val notecontent = noteET.text.toString()
            val user = User("",notecontent)
            val notetitle = intent.getStringExtra("key")
            db.insertNoteData(user, notetitle)
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}