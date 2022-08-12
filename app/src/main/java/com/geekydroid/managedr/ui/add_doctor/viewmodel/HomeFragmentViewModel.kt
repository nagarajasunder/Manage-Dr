package com.geekydroid.managedr.ui.add_doctor.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.Doctor
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.repository.HomeFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(private val repository: HomeFragmentRepository) :
    ViewModel() {

    private var _doctorData: MutableLiveData<Resource<List<HomeScreenDoctorData>>> = MutableLiveData(Resource.Loading())
    val doctorData:LiveData<Resource<List<HomeScreenDoctorData>>> = _doctorData

    init {
        getDoctorData()
    }

    private fun getDoctorData()
    {
        viewModelScope.launch {
            _doctorData.postValue(Resource.Loading(null))
            repository.getDoctorData().collect{ data ->
                _doctorData.postValue(Resource.Success(data))
            }
        }
    }


    private val homeFragmentEventsChannel = Channel<HomeFragmentEvents>()
    val eventsChannel = homeFragmentEventsChannel.receiveAsFlow()

    fun onFabClicked() = viewModelScope.launch {
        homeFragmentEventsChannel.send(HomeFragmentEvents.addNewDoctorFabClicked)
    }

    fun onDoctorClicked(data: HomeScreenDoctorData) = viewModelScope.launch {
        homeFragmentEventsChannel.send(HomeFragmentEvents.navigateToDoctorDashboard(data.doctorID))
    }


}

sealed class HomeFragmentEvents {
    object addNewDoctorFabClicked : HomeFragmentEvents()
    data class navigateToDoctorDashboard(val doctorId:Int) : HomeFragmentEvents()
}