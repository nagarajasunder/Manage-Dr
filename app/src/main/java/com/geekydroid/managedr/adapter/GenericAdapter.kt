package com.geekydroid.managedr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.databinding.DoctorCardBinding
import com.geekydroid.managedr.providers.TemplateProvider
import com.geekydroid.managedr.ui.add_doctor.model.Doctor
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData

class GenericAdapter(private val items:List<HomeScreenDoctorData>,private val layoutId:Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context),layoutId,parent,false)
        return TemplateProvider.getViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TemplateProvider.bindView(holder,items[position])
    }

    override fun getItemCount() = items.size
}