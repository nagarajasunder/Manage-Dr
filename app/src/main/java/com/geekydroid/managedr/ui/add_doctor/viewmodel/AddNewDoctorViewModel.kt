package com.geekydroid.managedr.ui.add_doctor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ui.add_doctor.model.Doctor
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewDoctorViewModel @Inject constructor(private val repository: DoctorRepository) :
    ViewModel() {

    private val AddNewDoctorEventsChannel = Channel<AddNewDoctorEvents>()
    val AddNewDoctorEvent = AddNewDoctorEventsChannel.receiveAsFlow()

    val doctorName = MutableLiveData("")
    val hospitalName = MutableLiveData("")
    val specialization = MutableLiveData("")

    fun onSaveCtaClicked() = viewModelScope.launch {
        AddNewDoctorEventsChannel.send(AddNewDoctorEvents.SaveNewDoctor)
    }

    fun validateAndSaveNewDoctor() = viewModelScope.launch {
        if (doctorName.value.toString().isEmpty()) {
            AddNewDoctorEventsChannel.send(AddNewDoctorEvents.EnterDoctorName)
        } else {
            saveNewDoctor()
            AddNewDoctorEventsChannel.send(AddNewDoctorEvents.DoctorSavedSuccessFully)
        }
    }

    private suspend fun saveNewDoctor() {
        val newDoctor = Doctor(
            doctorName = doctorName.value.toString(),
            hospitalName = hospitalName.value ?: "",
            specialization = specialization.value ?: ""
        )
        repository.addNewDoctor(newDoctor)
    }

}

sealed class AddNewDoctorEvents {
    object SaveNewDoctor : AddNewDoctorEvents()
    object EnterDoctorName : AddNewDoctorEvents()
    object DoctorSavedSuccessFully : AddNewDoctorEvents()
}