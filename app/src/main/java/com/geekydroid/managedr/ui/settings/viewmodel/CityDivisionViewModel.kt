package com.geekydroid.managedr.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import com.geekydroid.managedr.ui.settings.repository.SettingsRepository
import com.geekydroid.managedr.ui.settings.ui.CityDivisionFragment
import com.geekydroid.managedr.utils.DialogInputType
import com.geekydroid.managedr.utils.TextUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CityDivisionViewModel"
@HiltViewModel
class CityDivisionViewModel @Inject constructor(private val repository:SettingsRepository) : ViewModel() {

    private val eventsChannel:Channel<CityDivisionFragmentEvents> = Channel()
    val events:Flow<CityDivisionFragmentEvents> = eventsChannel.receiveAsFlow()

    private val _editData: MutableLiveData<Resource<List<SettingsEditData>>> =
        MutableLiveData(Resource.Loading())
    val editData: LiveData<Resource<List<SettingsEditData>>> = _editData
    private val selectedType = MutableStateFlow(DialogInputType.CITY.name)
    private val selectedTypeLiveData = MutableLiveData(DialogInputType.CITY.name)

    private var cityData:MutableLiveData<List<String>> = MutableLiveData(listOf())
    private var divisionData:MutableLiveData<List<String>> = MutableLiveData(listOf())

    init {
        getData()
        getAllCities()
        getAllDivisions()
    }

    private fun getAllDivisions() {
        viewModelScope.launch {
            repository.getAllDivisionNames().collect{
                divisionData.postValue(it)
            }
        }
    }

    private fun getAllCities() {
        viewModelScope.launch {
            repository.getAllCityNames().collect{
                cityData.postValue(it)
            }
        }
    }

    private fun getData() = viewModelScope.launch {
        _editData.postValue(Resource.Loading())
        selectedType.flatMapLatest { type ->
            selectedTypeLiveData.postValue(type)
            repository.getEditableData(type)
        }.collect{
            _editData.postValue(Resource.Success(it))
        }
    }

    fun updateEditType(editType: DialogInputType) {
        selectedType.value = editType.name
    }

    fun checkForDuplicateInput(input: String, data: SettingsEditData) {
        viewModelScope.launch {
            editData.value?.let {
                when(it)
                {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data!!.map { data -> data.name.lowercase() }.contains(input.lowercase()))
                        {
                            eventsChannel.send(CityDivisionFragmentEvents.showDuplicateInputError(input))
                        }
                        else
                        {
                            updateInput(data,input)
                        }
                    }
                }
            }
        }
    }

    private fun updateInput(data: SettingsEditData, input: String) {
        viewModelScope.launch {
            if (data.name == input)
            {
                eventsChannel.send(CityDivisionFragmentEvents.DismissDialog)
            }
            else
            {
                if (data.type == DialogInputType.CITY.name)
                {
                    val city = MdrCity(cityId = data.id,
                        cityName = TextUtils.trimText(input),
                        createdOn = data.createdOn,
                        updatedOn = System.currentTimeMillis())
                    repository.updateCity(city)
                    eventsChannel.send(CityDivisionFragmentEvents.DismissDialog)
                }
                else
                {
                    val division = MdrCategory(categoryID = data.id,
                        categoryName = TextUtils.trimText(input),
                        createdOn = data.createdOn,
                        updatedOn = System.currentTimeMillis())
                    repository.updateDivision(division)
                    eventsChannel.send(CityDivisionFragmentEvents.DismissDialog)
                }
            }
        }
    }

    fun deleteData(data: SettingsEditData) {
        viewModelScope.launch {
            if (data.type == DialogInputType.CITY.name)
            {
                repository.deleteCityWithTransactions(data.id)
            }
            else
            {
                repository.deleteDivisionWithTransactions(data.id)
            }
        }
    }

    fun isCityDuplicate(input: String) {
       viewModelScope.launch {
           if (cityData.value.isNullOrEmpty())
           {
               addNewCity(input)
               eventsChannel.send(CityDivisionFragmentEvents.DismissAddNewItemDialog)
           }
           else
           {
               if (cityData.value!!.map { it.lowercase() }.contains(input.lowercase()))
               {
                   eventsChannel.send(CityDivisionFragmentEvents.showDuplicateInputError(input))
               }
               else
               {
                   addNewCity(input)
                   eventsChannel.send(CityDivisionFragmentEvents.DismissAddNewItemDialog)
               }
           }
       }
    }

    private suspend fun addNewCity(input: String) {
        val newCity = MdrCity(cityName = TextUtils.trimText(input))
        repository.addNewCity(newCity)
    }

    fun isDuplicateDivision(input: String) {
        viewModelScope.launch {
            if (divisionData.value.isNullOrEmpty())
            {
                addNewDivision(input)
                eventsChannel.send(CityDivisionFragmentEvents.DismissAddNewItemDialog)
            }
            else
            {
                if (divisionData.value!!.map { it.lowercase() }.contains(input.lowercase()))
                {
                    eventsChannel.send(CityDivisionFragmentEvents.showDuplicateInputError(input))
                }
                else
                {
                    addNewDivision(input)
                    eventsChannel.send(CityDivisionFragmentEvents.DismissAddNewItemDialog)
                }
            }
        }
    }

    private suspend fun addNewDivision(input: String) {
        val newDivision = MdrCategory(categoryName = TextUtils.trimText(input))
        repository.addNewDivision(newDivision)
    }
}

sealed class CityDivisionFragmentEvents
{
    data class showDuplicateInputError(val input:String) : CityDivisionFragmentEvents()
    object DismissDialog : CityDivisionFragmentEvents()
    object DismissAddNewItemDialog : CityDivisionFragmentEvents()
}