package com.geekydroid.managedr.providers

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

object TemplateProvider {

    fun getViewHolder(binding:ViewDataBinding): RecyclerView.ViewHolder = CardViewHolder(binding)

}