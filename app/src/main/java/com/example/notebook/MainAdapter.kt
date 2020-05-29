package com.example.notebook


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class MainAdapter(private val dataset: MutableList<String>) :
    RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    private var removedPosition: Int = 0
    private var removedItem: String = ""


    class MainViewHolder (v: View) : RecyclerView.ViewHolder(v) {
        var label: TextView = v.findViewById<TextView>(R.id.item_name)
        // add content just like for label (This is where the notes wil lbe)

        init{
            v.setOnClickListener{
                val intent = Intent(v.context, NoteActivity::class.java)
                intent.putExtra("key", label.text)
                v.context.startActivity(intent)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.label.text = dataset[position]
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedItem = dataset[viewHolder.adapterPosition]
        dataset.removeAt(viewHolder.adapterPosition)
        notifyItemRemoved(viewHolder.adapterPosition)
        Snackbar.make(viewHolder.itemView, "$removedItem deleted.", Snackbar.LENGTH_LONG)
    }

    override fun getItemCount() = dataset.size
}

