package com.geekydroid.managedr.providers

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.application.ScreenData
import java.lang.Exception

object TemplateProvider {

    fun getViewHolder(binding:ViewDataBinding): RecyclerView.ViewHolder = CardViewHolder(binding)
    fun bindView(
        holder: RecyclerView.ViewHolder,
        screenData: ScreenData? = null
    ) {
        try {
            if (screenData != null)
            {
                (holder as CardViewHolder).bind(screenData)
            }
        }
        catch (e:Exception)
        {

        }
    }

}