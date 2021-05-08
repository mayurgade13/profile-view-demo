package com.example.profileview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for providing data to the recycler view which shows list of items
 **/
class RecyclerViewAdapter(
    private val itemList: List<String>,
) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val name = itemList[position]
        holder.name.text = name
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class MyViewHolder(listItem: View) : RecyclerView.ViewHolder(listItem) {
        val name: TextView = listItem.findViewById(R.id.name)
    }
}
