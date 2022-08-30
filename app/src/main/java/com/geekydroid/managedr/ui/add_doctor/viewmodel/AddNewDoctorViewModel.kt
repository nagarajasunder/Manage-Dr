package com.geekydroid.managedr.ui.add_doctor.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.providers.DateFormatProvider
import com.geekydroid.managedr.ui.add_doctor.model.MdrDoctor
import com.geekydroid.managedr.ui.add_doctor.repository.DoctorRepository
import com.geekydroid.managedr.utils.DateUtils
import com.geekydroid.managedr.utils.TextUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewDoctorViewModel @Inject constructor(private val repository: DoctorRepository) :
    ViewModel() {

    private var existingDoctorId: Int = -1
    private var existingDoctor: MdrDoctor? = null
    private val AddNewDoctorEventsChannel = Channel<AddNewDoctorEvents>()
    val AddNewDoctorEvent = AddNewDoctorEventsChannel.receiveAsFlow()

    val doctorName = MutableLiveData("")
    val hospitalName = MutableLiveData("")
    val mobileNumber = MutableLiveData("")
    var dateOfBirth = MutableLiveData("")
    private var dateOfBirthLong: Long = 0L
    var weddingAnniversaryDate = MutableLiveData("")
    private var weddingAnniversaryDateLong: Long = 0L

    fun updateExistingDoctorId(doctorId:Int)
    {
        existingDoctorId = doctorId
        if (doctorId != -1)
        {
            getDoctorDetails()
        }
    }

    fun getDoctorDetails() {
        viewModelScope.launch {
            repository.getDoctorById(existingDoctorId).first().let {
                existingDoctor = it
                prefillDoctorDetails()
            }
        }
    }

    private fun prefillDoctorDetails() {
        existingDoctor?.let { doctor ->
            doctorName.postValue(doctor.doctorName)
            hospitalName.postValue(doctor.hospitalName)
            mobileNumber.postValue(doctor.doctorMobileNumber)
            dateOfBirthLong = doctor.dateOfBirth?.time ?: 0L
            dateOfBirth.postValue(if (dateOfBirthLong == 0L) "" else DateUtils.fromLongToDateString(
                dateOfBirthLong,
                DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY))
            weddingAnniversaryDateLong = doctor.weddingAnniversaryDate?.time ?: 0L
            weddingAnniversaryDate.postValue(if (weddingAnniversaryDateLong == 0L) "" else DateUtils.fromLongToDateString(
                weddingAnniversaryDateLong,
                DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY))

        }
    }

    fun onSaveCtaClicked() = viewModelScope.launch {
        AddNewDoctorEventsChannel.send(AddNewDoctorEvents.SaveNewDoctor)
    }

    fun validateAndSaveNewDoctor() = viewModelScope.launch {
        if (doctorName.value.toString().isEmpty() || (doctorName.value.toString()
                .isNotEmpty() && doctorName.value.toString().length > 20)
        ) {
            AddNewDoctorEventsChannel.send(AddNewDoctorEvents.EnterDoctorName)
        } else if (mobileNumber.value.toString()
                .isNotEmpty() && mobileNumber.value.toString().length > 15
        ) {
            AddNewDoctorEventsChannel.send(AddNewDoctorEvents.EnterValidMobileNumber)
        } else {
            if (existingDoctorId == -1) {
                saveNewDoctor()
                AddNewDoctorEventsChannel.send(AddNewDoctorEvents.DoctorSavedSuccessFully)
            } else {
                updateDoctor()
                AddNewDoctorEventsChannel.send(AddNewDoctorEvents.DoctorUpdatedSuccessfully)
            }
        }
    }

    private suspend fun updateDoctor() {
        existingDoctor?.let { doctor ->
                doctor.copy(
                    doctorName = TextUtils.trimText(doctorName.value ?: ""),
                    dateOfBirth = DateUtils.fromLongToDate(dateOfBirthLong),
                    weddingAnniversaryDate = DateUtils.fromLongToDate(weddingAnniversaryDateLong),
                    doctorMobileNumber = TextUtils.trimText(mobileNumber.value ?: ""),
                    hospitalName = TextUtils.trimText(hospitalName.value ?: ""),
                    updatedOn = System.currentTimeMillis()
                )
                repository.updateDoctor(doctor)
            }
    }

    private suspend fun saveNewDoctor() {
        val newMdrDoctor = MdrDoctor(
            doctorName = TextUtils.trimText(doctorName.value),
            hospitalName = TextUtils.trimText(hospitalName.value),
            dateOfBirth = DateUtils.fromLongToDate(dateOfBirthLong),
            weddingAnniversaryDate = DateUtils.fromLongToDate(weddingAnniversaryDateLong),
            doctorMobileNumber = TextUtils.trimText(mobileNumber.value)
        )
        repository.addNewDoctor(newMdrDoctor)
    }

    fun dobClicked() = viewModelScope.launch {
        AddNewDoctorEventsChannel.send(AddNewDoctorEvents.OpenDobPicker)
    }

    fun weddingDateClicked() = viewModelScope.launch {
        AddNewDoctorEventsChannel.send(AddNewDoctorEvents.OpenWeddingPicker)
    }

    fun updateDob(dob: Long) {
        dateOfBirthLong = dob
        dateOfBirth.value =
            DateUtils.fromLongToDateString(dob, DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
    }

    fun updateWeddingDate(weddingDate: Long) {
        weddingAnniversaryDateLong = weddingDate
        weddingAnniversaryDate.value = DateUtils.fromLongToDateString(weddingAnniversaryDateLong,
            DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
    }
}

sealed class AddNewDoctorEvents {
    object SaveNewDoctor : AddNewDoctorEvents()
    object EnterDoctorName : AddNewDoctorEvents()
    object DoctorSavedSuccessFully : AddNewDoctorEvents()
    object EnterValidMobileNumber : AddNewDoctorEvents()
    object OpenDobPicker : AddNewDoctorEvents()
    object OpenWeddingPicker : AddNewDoctorEvents()
    object DoctorUpdatedSuccessfully : AddNewDoctorEvents()
}