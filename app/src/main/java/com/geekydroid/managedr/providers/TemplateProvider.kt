package com.geekydroid.managedr.providers

import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.geekydroid.managedr.application.ScreenData
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.utils.UiOnClickListener

private const val TAG = "TemplateProvider"
object TemplateProvider {

    fun getViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder = CardViewHolder(binding)
    fun bindView(
        holder: RecyclerView.ViewHolder,
        screenData: ScreenData? = null,
        listener: UiOnClickListener? = null
    ) {
        try {
            if (screenData != null) {
                (holder as CardViewHolder).bind(screenData)
            }
            if (listener != null) {
                (holder as CardViewHolder).bind(listener)
            }
        } catch (e: Exception) {

        }
    }

    fun areItemsSame(oldItem: ScreenData, newItem: ScreenData): Boolean {
        if (oldItem is HomeScreenDoctorData && newItem is HomeScreenDoctorData) {
            return oldItem.doctorID == newItem.doctorID
        }
        else if (oldItem is DoctorDashboardTxData && newItem is DoctorDashboardTxData)
        {
            return oldItem.transactionId == newItem.transactionId
        }
        return false
    }

    fun areContentsSame(oldItem: ScreenData, newItem: ScreenData): Boolean {
        if (oldItem is HomeScreenDoctorData && newItem is HomeScreenDoctorData) {
            return oldItem == newItem
        }
        else if (oldItem is DoctorDashboardTxData && newItem is DoctorDashboardTxData)
        {
            return oldItem == newItem
        }
        return false
    }

}