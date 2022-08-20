package com.geekydroid.managedr.ui.doctordashboard.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.doctordashboard.repository.DoctorDashboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorDashboardViewmodel @Inject constructor(private val repository: DoctorDashboardRepository) : ViewModel() {

    private val _transactionData:MutableLiveData<Resource<List<DoctorDashboardTxData>>> = MutableLiveData(Resource.Loading())
    val transactionData:LiveData<Resource<List<DoctorDashboardTxData>>> = _transactionData

    private val doctorDashboardEventsChannel:Channel<doctorDashboardEvents> = Channel()
    val doctorDashboardEvent = doctorDashboardEventsChannel.receiveAsFlow()

    private val _cityData:MutableLiveData<List<MdrCity>> = MutableLiveData(listOf())
    val cityData:LiveData<List<MdrCity>> = _cityData

    init {
        getAllCities()
    }

    fun getTransactionData(doctorId: Int) = viewModelScope.launch {
        _transactionData.postValue(Resource.Loading())
        repository.getTransactionDataBasedOnFilters(doctorId).collect{
            _transactionData.postValue(Resource.Success(it))
        }
    }

    private fun getAllCities() = viewModelScope.launch {
        repository.getAllCities().collect{
            _cityData.postValue(it)
        }
    }

    private val _doctorData: MutableLiveData<Resource<HomeScreenDoctorData>> =
        MutableLiveData(Resource.Loading())
    val doctorData: LiveData<Resource<HomeScreenDoctorData>> = _doctorData

    fun getDoctorDataById(doctorId: Int) = viewModelScope.launch {
        _doctorData.postValue(Resource.Loading())
        repository.getDoctorData(doctorId).collect {
            _doctorData.postValue(Resource.Success(it))
        }
    }

    fun AddNewServiceOnClick() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.addNewServiceClicked)
    }

    fun AddNewCollectionOnClick() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.addNewCollectionClicked)
    }
}

sealed class doctorDashboardEvents
{
    object addNewServiceClicked : doctorDashboardEvents()
    object addNewCollectionClicked: doctorDashboardEvents()
}
