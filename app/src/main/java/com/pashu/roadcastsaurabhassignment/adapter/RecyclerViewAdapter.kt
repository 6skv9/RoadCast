package com.pashu.roadcastsaurabhassignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pashu.roadcastsaurabhassignment.R
import com.pashu.roadcastsaurabhassignment.data.Entries
import com.pashu.roadcastsaurabhassignment.databinding.RowEntryBinding

class RecyclerViewAdapter (
    val entryList: MutableList<Entries>?,
    val clickEntry:SelectEntry
    ) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {


    interface SelectEntry{
//        fun selectEntry(pos:Int)
        fun notifyPageNumber()
    }

    inner class MyViewHolder(private val binding: RowEntryBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(entry : Entries){
            binding.apply {
                binding.api.setText("API:- ${entry.API}")
                binding.link.setText("Link:- ${entry.Link}")
                binding.description.setText("Description:- ${entry.Description}")
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: RowEntryBinding = RowEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        val itemView = LayoutInflater.from(parent.context)
//            .inflate(R.layout.row_entry, parent, false)


        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val entry = entryList!![position]
        holder.bind(entry)

        if (position == entryList.size - 1) {
            clickEntry.notifyPageNumber()
        }

    }

    override fun getItemCount(): Int {
        return entryList!!.size
    }

    fun updateData(viewModels: List<Entries>) {
        entryList?.clear()
        entryList?.addAll(viewModels)
        notifyDataSetChanged()

    }
}