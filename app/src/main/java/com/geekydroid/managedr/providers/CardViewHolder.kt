package com.geekydroid.managedr.providers

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.utils.UiOnClickListener
import com.geekydroid.managedr.application.ScreenData

class CardViewHolder(private val binding:ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(obj:Any? = null)
    {
        if (obj != null)
        {
            if (obj is ScreenData)
            {
                binding.setVariable(BR.model,obj)
            }
            else if (obj is UiOnClickListener)
            {
                binding.setVariable(BR.listener,obj)
            }
        }
        binding.executePendingBindings()
    }

}