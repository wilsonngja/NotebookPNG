package com.example.notebook

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast


val DATABASE_NAME = "MyDB"
val TABLE_NAME = "Users"
val COL_TITLE = "Title"
val COL_CONTENT = "Content"


class DatabaseHandler (var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null,1){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_TITLE + " VARCHAR(256)," +
                COL_CONTENT + " TEXT)"
        db?.execSQL(createTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }


    fun insertData(user : User){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_TITLE,user.title)
        cv.put(COL_CONTENT,user.content)
        val result = db.insert(TABLE_NAME,null,cv)
        if (result == -1.toLong())
            Toast.makeText(context, "Creation failed", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Successfully created",Toast.LENGTH_LONG).show()
    }

    fun insertNoteData(user : User, title : String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_CONTENT,user.content)
        db.update(TABLE_NAME,cv, COL_TITLE + " = ?", arrayOf(title))
    }

    fun readData() : MutableList<User>{
        val list : MutableList<User> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * from " + TABLE_NAME
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()){
            do {
                val user = User()
                val read_result = result.getString(result.getColumnIndex(COL_TITLE))

                if (read_result != null) {
                    user.title = result.getString(result.getColumnIndex(COL_TITLE))
                }

                list.add(user)
            }while (result.moveToNext())
        }
        result.close()
        db.close()

        return list
    }

    @SuppressLint("Recycle")
    fun readNoteData(title:String) : String{
        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TITLE + " =? "
        val result = db.rawQuery(query, arrayOf(title))
        if (result.moveToFirst()){
            val content = result.getColumnIndex(COL_CONTENT)
            return result.getString(content)
        }

        else
        {
            return ""
        }
    }

    fun deleteData(title : String){
        val TAG = "MYACTIVITY"
        val db = this.readableDatabase
        Log.i(TAG,"The value is $title")
        db.delete(TABLE_NAME, COL_TITLE + " = ?", arrayOf(title))
    }

}

