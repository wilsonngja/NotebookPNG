package com.example.notebook

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.file_dialog.view.*
import kotlinx.android.synthetic.main.file_dialog.view.fileET


class MainActivity : AppCompatActivity() {

    private lateinit var  viewAdapter : MainAdapter
    private lateinit var  viewManager : RecyclerView.LayoutManager
    private lateinit var  deleteIcon: Drawable
    private var swipeBackground: ColorDrawable = ColorDrawable(Color.parseColor("#FF6961"))
    val dataset: MutableList<String> = arrayListOf()

    companion object{
        private val TAG = MainActivity::class.java.simpleName
        private const val LIST_KEY = "LIST_KEY"
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.mainpage_toolbar))

        viewAdapter = MainAdapter(dataset)
        viewManager = LinearLayoutManager(this)

        deleteIcon = ContextCompat.getDrawable(this,R.drawable.ic_delete)!!

        recycler_view.apply{
            setHasFixedSize(true)
            adapter = viewAdapter
            layoutManager = viewManager
            var itemDecoration = DividerItemDecoration(this.context,DividerItemDecoration.VERTICAL)
            itemDecoration.setDrawable(getDrawable(R.drawable.divider)!!)
            addItemDecoration(itemDecoration)
        }

        val context = this
        var db = DatabaseHandler(context)
        var data = db.readData()

        for (i in 0..(data.size-1)){
            dataset.add(data.get(i).title)
        }
        newNoteBtn.setOnClickListener {
            //Inflate Dialog
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.file_dialog,null)
            //Alert Dialog Builder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
                .setTitle("CREATE NOTEBOOK")
            //Show Dialog
            val mAlertDialog = mBuilder.show()
            val notetitle = mDialogView.fileET.text.toString()
            /*
            val hasVal = notetitle in data
            if (notetitle in data){
                Toast.makeText(this, "Name already exist. Please choose other name.", Toast.LENGTH_LONG)

                mDialogView.dialogOkBtn.isEnabled = false
            }
             */

            //OK Button
            mDialogView.dialogOkBtn.setOnClickListener {

                if (notetitle.isNotEmpty()){
                    mAlertDialog.dismiss()
                }
                var user = User(notetitle,"")
                db.insertData(user)
                dataset.add(notetitle as String)
                viewAdapter.notifyDataSetChanged()
                Log.i("TEST","dataset value is $dataset")
            }
            //Cancel Button
            mDialogView.dialogCancelBtn.setOnClickListener {
                //Dismiss Button
                mAlertDialog.dismiss()
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val delete_item = dataset[viewHolder.adapterPosition]
                Log.i("TEST","The delete_item value is: $delete_item")
                (viewAdapter as MainAdapter).removeItem(viewHolder)
                db.deleteData(delete_item)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                val iconMargin = (itemView.height-deleteIcon.intrinsicHeight)/2
                if (dX > 0){
                    swipeBackground.setBounds(itemView.left,itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left+iconMargin, itemView.top + iconMargin, itemView.left + iconMargin + deleteIcon.intrinsicWidth,itemView.bottom - iconMargin)
                }
                else
                {
                    swipeBackground.setBounds(itemView.right+dX.toInt(),itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right-iconMargin - deleteIcon.intrinsicWidth, itemView.top + iconMargin, itemView.right - iconMargin  ,itemView.bottom - iconMargin)
                }

                swipeBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left,itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right+dX.toInt(),itemView.top, itemView.right, itemView.bottom)
                deleteIcon.draw(c)

                c.restore()

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recycler_view)


    }

}
