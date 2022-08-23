package com.geekydroid.managedr.providers

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.utils.UiOnClickListener
import com.geekydroid.managedr.application.ScreenData

private const val TAG = "HomeFragment"
class CardViewHolder(private val binding:ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(obj:Any? = null)
    {
        if (obj != null)
        {
            if (obj is ScreenData)
            {
                Log.d(TAG, "bind: binding $obj")
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