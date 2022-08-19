package com.geekydroid.managedr.ui.addnewservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import com.geekydroid.managedr.ui.addnewservice.repository.AddNewServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewServiceFragmentViewmodel @Inject constructor(private val repository: AddNewServiceRepository) :
    ViewModel() {


    private val newServiceEventsChannel: Channel<NewServiceFragmentEvents> = Channel()
    val newServiceEvents = newServiceEventsChannel.receiveAsFlow()

    private val _cityData: MutableLiveData<List<MdrCity>> = MutableLiveData(listOf())
    val cityData: LiveData<List<MdrCity>> = _cityData

    private val _categoryData: MutableLiveData<List<MdrCategory>> = MutableLiveData(listOf())
    val categoryData: LiveData<List<MdrCategory>> = _categoryData

    init {
        getAllCities()
        getAllDivisions()
    }

    private fun getAllDivisions() = viewModelScope.launch {
        repository.getAllDivisionNames().collect {
            _categoryData.postValue(it)
        }
    }

    val _doctorName: MutableLiveData<String> = MutableLiveData("")
    val doctorName: LiveData<String> = _doctorName

    private val _city: MutableLiveData<String> = MutableLiveData("")
    val city: LiveData<String> = _city

    val _transactionAmount: MutableLiveData<String> = MutableLiveData("")
    val transactionAmount: LiveData<String> = _transactionAmount

    private var selectedCityId: Int = -1
    private var selectedCategoryId: Int = -1

    fun getAllCities() = viewModelScope.launch {
        repository.getAllCityNames().collect {
            _cityData.postValue(it)
        }
    }

    fun datePickerClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showDatePickerDialog)
    }

    fun addNewCategoryClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showNewCategoryDialog)
    }

    fun addNewCityClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showNewCityDialog)
    }

    fun getDoctorName(doctorId: Int) = viewModelScope.launch {
        repository.getDoctorName(doctorId).first().let {
            _doctorName.postValue(it)
        }
    }

    fun isCityDuplicate(input: String) = viewModelScope.launch {
        if (cityData.value.isNullOrEmpty()) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        } else if (cityData.value!!.map {
                it.cityName.lowercase()
            }.contains(input.lowercase())) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.showDuplicateWarningInDialog(input))
        }
    }

    fun isDuplicateDivision(input: String) = viewModelScope.launch {
        if (categoryData.value.isNullOrEmpty()) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        } else if (
            categoryData.value!!.map {
                it.categoryName.lowercase()
            }.contains(input.lowercase())
        ) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.showDuplicateWarningInDialog(input))
        }
    }

    fun addNewCity(input: String) = viewModelScope.launch {
        val city = MdrCity(cityName = input,
            createdOn = System.currentTimeMillis(),
            updatedOn = System.currentTimeMillis())
        repository.addNewCity(city)
    }

    fun addNewCategory(category: String) = viewModelScope.launch {
        val newCategory = MdrCategory(categoryName = category)
        repository.addNewCategory(newCategory)
    }

    fun onSaveCtaClicked() = viewModelScope.launch {
        if (selectedCityId == -1) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.selectCityError)
        } else if (selectedCategoryId == -1) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.selectCategoryError)
        } else if (_transactionAmount.value.isNullOrEmpty()) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.transactionAmountError)
        } else {
            addNewService()
            newServiceEventsChannel.send(NewServiceFragmentEvents.newServiceCreated)
        }
    }

    private fun addNewService() {
        viewModelScope.launch {
            val newService = MdrService(servicedDoctorId = 1,
                categoryId = selectedCategoryId,
                cityId = selectedCityId,
                serviceAmount = _transactionAmount.value?.toInt() ?: 0)
            repository.addNewService(newService)
        }
    }

    fun updateSelectedCity(index: Int) {
        selectedCityId = if (index == 0) {
            -1
        } else {
            index - 1
        }
    }

    fun updateSelectedCategory(index: Int) {
        selectedCategoryId = if (index == 0) {
            -1
        } else {
            index - 1
        }
    }

}

sealed class NewServiceFragmentEvents {
    object showNewCategoryDialog : NewServiceFragmentEvents()
    object showNewCityDialog : NewServiceFragmentEvents()
    object showDatePickerDialog : NewServiceFragmentEvents()
    object selectCityError : NewServiceFragmentEvents()
    object selectCategoryError : NewServiceFragmentEvents()
    object transactionAmountError : NewServiceFragmentEvents()
    object newServiceCreated : NewServiceFragmentEvents()
    object dismissNewDivisionDialog : NewServiceFragmentEvents()
    data class showDuplicateWarningInDialog(val input: String) : NewServiceFragmentEvents()
}