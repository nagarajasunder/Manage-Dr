package com.geekydroid.managedr.ui.addnewservice.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.application.TransactionType
import com.geekydroid.managedr.providers.DateFormatProvider
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.addnewservice.model.MdrService
import com.geekydroid.managedr.ui.addnewservice.repository.AddNewServiceRepository
import com.geekydroid.managedr.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddNewServiceFragmentViewmodel @Inject constructor(private val repository: AddNewServiceRepository) :
    ViewModel() {


    private val newServiceEventsChannel: Channel<NewServiceFragmentEvents> = Channel()
    val newServiceEvents = newServiceEventsChannel.receiveAsFlow()

    private val _cityData: MutableLiveData<List<MdrCity>> = MutableLiveData(listOf<MdrCity>())
    val cityData: LiveData<List<MdrCity>> = _cityData

    private val _categoryData: MutableLiveData<List<MdrCategory>> =
        MutableLiveData(listOf<MdrCategory>())
    val categoryData: LiveData<List<MdrCategory>> = _categoryData

    var citySpinnerIndex: Int? = null
        private set
    var categorySpinnerIndex: Int? = null
        private set

    private var transactionDate:Long = 0L
    var transactionDateFormatted:MutableLiveData<String> = MutableLiveData("")

    init {
        getAllCities()
        getAllDivisions()
    }

    private fun getAllDivisions() = viewModelScope.launch {
        repository.getAllDivisionNames().collect {
            _categoryData.value = it
        }
    }

    val _doctorName: MutableLiveData<String> = MutableLiveData("")
    val doctorName: LiveData<String> = _doctorName

    private val _city: MutableLiveData<String> = MutableLiveData("")
    val city: LiveData<String> = _city

    val _transactionAmount: MutableLiveData<String> = MutableLiveData("")
    val transactionAmount: LiveData<String> = _transactionAmount

    private var selectedCityIndex: Int = -1
    private var selectedCategoryIndex: Int = -1

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
            addNewCity(input)
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        } else if (cityData.value!!.map {
                it.cityName.lowercase()
            }.contains(input.lowercase())) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.showDuplicateWarningInDialog(input))
        } else {
            addNewCity(input)
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        }
    }

    fun isDuplicateDivision(input: String) = viewModelScope.launch {
        if (categoryData.value.isNullOrEmpty()) {
            addNewCategory(input)
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        } else if (
            categoryData.value!!.map {
                it.categoryName.lowercase()
            }.contains(input.lowercase())
        ) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.showDuplicateWarningInDialog(input))
        } else {
            addNewCategory(input)
            newServiceEventsChannel.send(NewServiceFragmentEvents.dismissNewDivisionDialog)
        }
    }

    fun addNewCity(input: String) = viewModelScope.launch {
        val city = MdrCity(cityName = input.trim())
        repository.addNewCity(city)
    }

    fun addNewCategory(category: String) = viewModelScope.launch {
        val newCategory = MdrCategory(categoryName = category.trim())
        repository.addNewCategory(newCategory)
    }

    fun onSaveCtaClicked(doctorId:Int,transactionType:TransactionType) = viewModelScope.launch {
        if (selectedCityIndex == -1) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.selectCityError)
        } else if (selectedCategoryIndex == -1) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.selectCategoryError)
        } else if (_transactionAmount.value.isNullOrEmpty()) {
            newServiceEventsChannel.send(NewServiceFragmentEvents.transactionAmountError)
        }
        else if (transactionDate == 0L)
        {
            newServiceEventsChannel.send(NewServiceFragmentEvents.transactionDateError)
        }
        else {
            addNewService(doctorId,transactionType)
        }
    }

    private fun addNewService(doctorId:Int,transactionType:TransactionType) {
        viewModelScope.launch {
            val newService = MdrService(
                servicedDoctorId = doctorId,
                serviceAmount = transactionAmount.value?.toInt() ?: 0,
                categoryId = categoryData.value!![selectedCategoryIndex].categoryID,
                cityId = cityData.value!![selectedCityIndex].cityId,
                serviceDate = Date(transactionDate),
                transactionType = transactionType.toString())
            repository.addNewService(newService)
            if (transactionType == TransactionType.SERVICE)
            {
                newServiceEventsChannel.send(NewServiceFragmentEvents.newServiceCreated)
            }
            else
            {
                newServiceEventsChannel.send(NewServiceFragmentEvents.newCollectionCreated)
            }
        }
    }

    fun updateSelectedCity(index: Int) {
        if (index > 0) {
            selectedCityIndex = index-1
            citySpinnerIndex = index
        }
        else
        {
            citySpinnerIndex = null
            selectedCityIndex = -1
        }
        Log.d("addNewService", "onItemSelected: vm $selectedCityIndex")
    }

    fun updateSelectedCategory(index: Int) {
        if (index > 0) {
            selectedCategoryIndex = index-1
            categorySpinnerIndex = index
        }
        else
        {
            selectedCategoryIndex = -1
            categorySpinnerIndex = null
        }
    }

    fun updateTransactionDate(date: Long) {
        transactionDate = date
        transactionDateFormatted.value = DateUtils.fromLongToDateString(transactionDate,DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
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
    object transactionDateError: NewServiceFragmentEvents()
    object newCollectionCreated:NewServiceFragmentEvents()
}