package com.geekydroid.managedr.ui.add_doctor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(): ViewModel() {

    private val homeFragmentEventsChannel = Channel<HomeFragmentEvents>()
    val eventsChannel = homeFragmentEventsChannel.receiveAsFlow()

    fun onFabClicked() = viewModelScope.launch {
        homeFragmentEventsChannel.send(HomeFragmentEvents.addNewDoctorFabClicked)
    }

}

sealed class HomeFragmentEvents
{
    object addNewDoctorFabClicked : HomeFragmentEvents()
}