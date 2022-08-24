package com.customgallery.GalleryView.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.customgallery.GalleryView.model.ItemModel
import com.customgallery.R
import com.customgallery.databinding.ItemSelectedImageRcyBinding

class SelectedImageRcyAdapter(context:Context,list:MutableList<ItemModel>,callback:Callback) : RecyclerView.Adapter<SelectedImageRcyAdapter.ViewHolder>() {

    val context:Context
    var list:MutableList<ItemModel>
    val callback:Callback

    init {
        this.context = context
        this.list = list
        this.callback = callback
    }

    class ViewHolder(binding:ItemSelectedImageRcyBinding) : RecyclerView.ViewHolder(binding.root) {
        val binding:ItemSelectedImageRcyBinding
        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_selected_image_rcy,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imgImage.setImageURI(list.get(position).uri)

        holder.binding.imgCancel.visibility = View.VISIBLE
        holder.binding.imgCancel.setOnClickListener {
            callback.onItemRemoved(position)
        }
    }

    interface Callback {
        fun onItemRemoved(position: Int)
    }
}