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
import com.geekydroid.managedr.ui.addnewservice.ui.NewServiceFragment
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

    private var existingTransactionId:Int = -1
    private var cityId:Int = -1
    private val newServiceEventsChannel: Channel<NewServiceFragmentEvents> = Channel()
    val newServiceEvents = newServiceEventsChannel.receiveAsFlow()

    private val _categoryData: MutableLiveData<List<MdrCategory>> =
        MutableLiveData(listOf<MdrCategory>())
    val categoryData: LiveData<List<MdrCategory>> = _categoryData


    private var transactionDate:Long = 0L
    var transactionDateFormatted:MutableLiveData<String> = MutableLiveData("")

    init {
        getAllDivisions()
    }

    private fun getAllDivisions() = viewModelScope.launch {
        repository.getAllDivisionNames().collect {
            _categoryData.value = it
        }
    }

    val _doctorName: MutableLiveData<String> = MutableLiveData("")
    val doctorName: LiveData<String> = _doctorName

    val _transactionAmount: MutableLiveData<String> = MutableLiveData("")
    val transactionAmount: LiveData<String> = _transactionAmount

    var selectedCategoryIndex: Int = -1
        private set

    fun datePickerClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showDatePickerDialog)
    }

    fun addNewCategoryClicked() = viewModelScope.launch {
        newServiceEventsChannel.send(NewServiceFragmentEvents.showNewCategoryDialog)
    }


    fun getDoctorName(doctorId: Int) = viewModelScope.launch {
        repository.getDoctorName(doctorId).first().let {
            _doctorName.postValue(it)
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

        if (selectedCategoryIndex == -1) {
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

    fun onDeleteCtaClicked()
    {
        viewModelScope.launch {
            newServiceEventsChannel.send(NewServiceFragmentEvents.showDeleteWarningDialog)
        }
    }

    private fun addNewService(doctorId:Int,transactionType:TransactionType) {
        viewModelScope.launch {
            val newService = MdrService(
                servicedDoctorId = doctorId,
                serviceAmount = transactionAmount.value?.toDouble() ?: 0.0,
                categoryId = categoryData.value!![selectedCategoryIndex].categoryID,
                cityId = cityId,
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


    fun updateSelectedCategory(index: Int) {
        selectedCategoryIndex = index
    }

    fun updateTransactionDate(date: Long) {
        transactionDate = date
        transactionDateFormatted.value = DateUtils.fromLongToDateString(transactionDate,DateFormatProvider.DATE_FORMAT_MMM_DD_YYYY)
    }

    fun setDoctorCity(doctorCityId: Int) {
        cityId = doctorCityId
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            if (existingTransactionId != -1)
            {
                repository.deleteTransaction(existingTransactionId)
                newServiceEventsChannel.send(NewServiceFragmentEvents.showTransactionDeletedMessage)
            }
        }
    }

    fun updateExistingTransactionId(transactionId: Int) {
        existingTransactionId = transactionId
    }


}

sealed class NewServiceFragmentEvents {
    object showNewCategoryDialog : NewServiceFragmentEvents()
    object showDatePickerDialog : NewServiceFragmentEvents()
    object selectCategoryError : NewServiceFragmentEvents()
    object transactionAmountError : NewServiceFragmentEvents()
    object newServiceCreated : NewServiceFragmentEvents()
    object dismissNewDivisionDialog : NewServiceFragmentEvents()
    data class showDuplicateWarningInDialog(val input: String) : NewServiceFragmentEvents()
    object transactionDateError: NewServiceFragmentEvents()
    object newCollectionCreated:NewServiceFragmentEvents()
    object showDeleteWarningDialog : NewServiceFragmentEvents()
    object showTransactionDeletedMessage : NewServiceFragmentEvents()
}