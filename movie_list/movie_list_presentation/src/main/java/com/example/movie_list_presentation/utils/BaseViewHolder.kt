package com.example.moviedb.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.movie_list_presentation.utils.BaseAdapter

class BaseViewHolder<T> internal constructor(private val binding: ViewBinding, private val expression:(T, ViewBinding, Int, BaseAdapter<T>)->Unit)
    : RecyclerView.ViewHolder(binding.root){
    fun bind(item:T, position: Int, baseAdapter: BaseAdapter<T>){
        expression(item,binding,position, baseAdapter)
    }
}