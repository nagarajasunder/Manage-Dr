package com.geekydroid.managedr.ui.doctordashboard.viewmodel

import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.*
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.doctordashboard.repository.DoctorDashboardRepository
import com.geekydroid.managedr.utils.DateUtils
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import javax.inject.Inject

@HiltViewModel
class DoctorDashboardViewmodel @Inject constructor(private val repository: DoctorDashboardRepository) :
    ViewModel() {

    private val _transactionData: MutableLiveData<Resource<List<DoctorDashboardTxData>>> =
        MutableLiveData(Resource.Loading())
    val transactionData: LiveData<Resource<List<DoctorDashboardTxData>>> = _transactionData

    private val _dateRange: MutableLiveData<String> = MutableLiveData("")
    val dateRange: LiveData<String> = _dateRange

    private val doctorDashboardEventsChannel: Channel<doctorDashboardEvents> = Channel()
    val doctorDashboardEvent = doctorDashboardEventsChannel.receiveAsFlow()

    var cityNames:List<String> = listOf()
    private val selectedCityIndices:MutableSet<Int> = mutableSetOf()
    val cityFilterText = MutableLiveData("City")
    var selectedCityData: BooleanArray = booleanArrayOf()

    var divisionNames:List<String> = listOf()
    private val selectedDivisionIndices:MutableSet<Int> = mutableSetOf()
    val divisionFilterText = MutableLiveData("Division")
    var selectedDivisionData:BooleanArray = booleanArrayOf()

    init {
        getAllCities()
        getAllDivisions()
        setDefaultDateRange()
    }

    private fun setDefaultDateRange() {
        _dateRange.value =
            "From ${DateUtils.fromLongToDateString(MaterialDatePicker.thisMonthInUtcMilliseconds())} To ${
                DateUtils.fromLongToDateString(MaterialDatePicker.todayInUtcMilliseconds())
            }"
    }

    fun getTransactionData(doctorId: Int) = viewModelScope.launch {
        _transactionData.postValue(Resource.Loading())
        repository.getTransactionDataBasedOnFilters(doctorId).collect {
            _transactionData.postValue(Resource.Success(it))
        }
    }

    private fun getAllCities() = viewModelScope.launch {
        repository.getAllCities().collect { cityList ->
            selectedCityData = BooleanArray(cityList.size)
            cityNames = cityList.map { it.cityName }
        }
    }

    private fun getAllDivisions() = viewModelScope.launch {
        repository.getAllCategories().collect { divisionList ->
            selectedDivisionData = BooleanArray(divisionList.size)
            divisionNames = divisionList.map { it.categoryName }
        }
    }

    private val _doctorData: MutableLiveData<Resource<HomeScreenDoctorData>> =
        MutableLiveData(Resource.Loading())
    val doctorData: LiveData<Resource<HomeScreenDoctorData>> = _doctorData

    fun getDoctorDataById(doctorId: Int) = viewModelScope.launch {
        _doctorData.postValue(Resource.Loading())
        repository.getDoctorData(doctorId).collect {
            _doctorData.postValue(Resource.Success(it))
        }
    }

    fun AddNewServiceOnClick() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.addNewServiceClicked)
    }

    fun AddNewCollectionOnClick() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.addNewCollectionClicked)
    }

    fun onFromFilterClicked() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.showDateRangePicker)
    }

    fun updateDateRange(dateRange: Pair<Long, Long>?) {
        dateRange?.let { it ->
            _dateRange.value =
                "From ${DateUtils.fromLongToDateString(it.first)} to ${
                    DateUtils.fromLongToDateString(it.second)
                }"
        }
    }

    fun addCity(index: Int) {
        selectedCityIndices.add(index)
    }

    fun removeCity(index: Int)
    {
        selectedCityIndices.remove(index)
    }

    fun showCitySelection() = viewModelScope.launch {
        if (selectedCityIndices.isNotEmpty())
        {
            val builder = StringBuilder()
            selectedCityIndices.forEachIndexed { index, i ->
                builder.append(cityNames[i])
                if (index != selectedCityIndices.size -1)
                {
                    builder.append(", ")
                }

            }
            cityFilterText.value = builder.toString()
        }
    }

    fun clearCitySelection() {
        selectedCityIndices.clear()
        selectedCityData = BooleanArray(cityNames.size) { false }
        cityFilterText.value = "City"
    }

    fun addDivision(index: Int) {
        selectedDivisionIndices.add(index)
    }

    fun removeDivision(index: Int)
    {
        selectedCityIndices.remove(index)
    }

    fun showDivisionSelection() = viewModelScope.launch {
        if (selectedDivisionIndices.isNotEmpty())
        {
            val builder = StringBuilder()
            selectedDivisionIndices.forEachIndexed { index, i ->
                builder.append(divisionNames[i])
                if (index != selectedDivisionIndices.size -1)
                {
                    builder.append(", ")
                }

            }
            divisionFilterText.value = builder.toString()
        }
    }

    fun clearDivisionSelection() {
        selectedDivisionIndices.clear()
        selectedDivisionData = BooleanArray(divisionNames.size) { false }
        divisionFilterText.value = "Division"
    }
}

sealed class doctorDashboardEvents {
    object addNewServiceClicked : doctorDashboardEvents()
    object addNewCollectionClicked : doctorDashboardEvents()
    object showDateRangePicker : doctorDashboardEvents()
}
