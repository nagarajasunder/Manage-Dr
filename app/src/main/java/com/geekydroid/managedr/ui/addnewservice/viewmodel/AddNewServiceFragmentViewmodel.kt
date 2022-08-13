package com.geekydroid.managedr.ui.addnewservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.addnewservice.repository.AddNewServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddNewServiceFragmentViewmodel @Inject constructor(private val repository: AddNewServiceRepository) : ViewModel() {


    private val newServiceEventsChannel:Channel<NewServiceFragmentEvents> = Channel()
    val newServiceEvents = newServiceEventsChannel.receiveAsFlow()

    private val _cityData:MutableLiveData<List<String>> = MutableLiveData(listOf())
    val cityData:LiveData<List<String>> = _cityData

    val _doctorName: MutableLiveData<String> = MutableLiveData("")
    val doctorName: LiveData<String> = _doctorName

    private val _category: MutableLiveData<String> = MutableLiveData("")
    val category: LiveData<String> = _category

    private val _city: MutableLiveData<String> = MutableLiveData("")
    val city: LiveData<String> = _city

    val _transactionAmount: MutableLiveData<String> = MutableLiveData("")
    val transactionAmount: LiveData<String> = _transactionAmount

    fun getAllCities() = viewModelScope.launch {
        repository.getAllCityNames().collect{
            _cityData.postValue(it)
        }
    }

    fun datePickerClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showDatePickerDialog)
    }

    fun addNewCategoryClicked()  = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showNewCategoryDialog)
    }

    fun addNewCityClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showNewCityDialog)
    }

    fun getDoctorName(doctorId:Int) = viewModelScope.launch {
        repository.getDoctorName(doctorId).first().let{
            _doctorName.postValue(it)
        }
    }

    fun isCityDuplicate(input:String):Boolean
    {
        return if (cityData.value.isNullOrEmpty()) {
            false
        } else {
            cityData.value!!.map {
                it.lowercase()
            }.contains(input.lowercase())
        }
    }

    fun addNewCity(input: String) = viewModelScope.launch {
        val city = MdrCity(cityName = input, createdOn = System.currentTimeMillis(), updatedOn = System.currentTimeMillis())
        repository.addNewCity(city)
    }
}

sealed class NewServiceFragmentEvents {
    object showNewCategoryDialog : NewServiceFragmentEvents()
    object showNewCityDialog : NewServiceFragmentEvents()
    object showDatePickerDialog : NewServiceFragmentEvents()
}