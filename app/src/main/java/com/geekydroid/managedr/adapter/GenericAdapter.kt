package com.geekydroid.managedr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.providers.TemplateProvider
import com.geekydroid.managedr.utils.UiOnClickListener

class GenericAdapter(
    private val listener: UiOnClickListener? = null,
    @LayoutRes private val layoutId: Int,
) : ListAdapter<ScreenData, RecyclerView.ViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false)
        return TemplateProvider.getViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TemplateProvider.bindView(holder, currentList[position], listener)
    }

    override fun getItemCount() = currentList.size

    class DiffUtilCallback : DiffUtil.ItemCallback<ScreenData>() {
        override fun areItemsTheSame(oldItem: ScreenData, newItem: ScreenData): Boolean {
            return TemplateProvider.areItemsSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: ScreenData, newItem: ScreenData): Boolean {
            return TemplateProvider.areContentsSame(oldItem, newItem)
        }

    }

}