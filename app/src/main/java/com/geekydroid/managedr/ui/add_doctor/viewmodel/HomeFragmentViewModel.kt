package com.geekydroid.managedr.ui.add_doctor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.add_doctor.model.SortPreferences
import com.geekydroid.managedr.ui.add_doctor.repository.HomeFragmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(private val repository: HomeFragmentRepository) :
    ViewModel() {

    private val searchText:MutableStateFlow<String> = MutableStateFlow("")
    private val sortOrder:MutableStateFlow<SortPreferences> = MutableStateFlow(SortPreferences.NEWEST_FIRST)
    private var _doctorData: MutableLiveData<Resource<List<HomeScreenDoctorData>>> = MutableLiveData(Resource.Loading())
    val doctorData:LiveData<Resource<List<HomeScreenDoctorData>>> = _doctorData

    init {
        getDoctorData()
    }

    private fun getDoctorData() = viewModelScope.launch {
        _doctorData.value = Resource.Loading(null)
        searchText.combine(sortOrder) { searchQuery, order ->
            Pair(searchQuery, order)
        }
            .flatMapLatest { (searchQuery, order) ->
                repository.getDoctorData(searchQuery, order)
            }.collect {
                _doctorData.value = Resource.Success(it)
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

    fun updateSearchText(newText: String) {
        searchText.value = newText
    }

    fun updateSortOrder(order:SortPreferences)
    {
        sortOrder.value = order
    }


}

sealed class HomeFragmentEvents {
    object addNewDoctorFabClicked : HomeFragmentEvents()
    data class navigateToDoctorDashboard(val doctorId:Int) : HomeFragmentEvents()
}