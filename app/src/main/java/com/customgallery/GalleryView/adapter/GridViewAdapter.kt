package com.customgallery.GalleryView.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.customgallery.GalleryView.model.ItemModel
import com.customgallery.R
import com.customgallery.databinding.ItemGridRecyclerBinding
import java.io.File

class GridViewAdapter(context: Context,list:MutableList<ItemModel>,callback:Callback) : RecyclerView.Adapter<GridViewAdapter.ViewHolder>() {
    val context:Context
    var list:MutableList<ItemModel>
    val callback:Callback

    init {
        this.context = context
        this.list = list
        this.callback = callback
    }

    class ViewHolder(binding:ItemGridRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding:ItemGridRecyclerBinding
        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        return ViewHolder(DataBindingUtil.inflate(inflater,R.layout.item_grid_recycler,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        list.get(position).position = position

        val uri = list.get(position).uri
        holder.binding.imgImage.setImageURI(uri,context)
        holder.binding.imgImage

        if (list.get(position).isSelected) {
            holder.binding.cnstMain.background = ContextCompat.getDrawable(context,R.drawable.border_selected)
            holder.binding.cnstMain.setPadding(2,2,2,2)
            holder.binding.txtCount.visibility = View.VISIBLE
            holder.binding.txtCount.setText(list.get(position).count.toString())
        } else {
            holder.binding.cnstMain.background = null
            holder.binding.cnstMain.setPadding(0,0,0,0)
            holder.binding.txtCount.visibility = View.GONE
        }

        holder.binding.imgImage.setOnClickListener {
            callback.onItemSelected(position)
        }
    }

    interface Callback {
        fun onItemSelected(position: Int)
    }
}