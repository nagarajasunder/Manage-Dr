package com.geekydroid.managedr.ui.doctordashboard.viewmodel

import android.util.Log
import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
import com.geekydroid.managedr.R
import com.geekydroid.managedr.application.TransactionType
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.add_doctor.model.HomeScreenDoctorData
import com.geekydroid.managedr.ui.addnewservice.model.MdrCategory
import com.geekydroid.managedr.ui.addnewservice.model.MdrCity
import com.geekydroid.managedr.ui.doctordashboard.model.DoctorDashboardTxData
import com.geekydroid.managedr.ui.doctordashboard.repository.DoctorDashboardRepository
import com.geekydroid.managedr.utils.DateUtils
import com.geekydroid.managedr.utils.TextUtils
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "DoctorDashboardViewmode"
@HiltViewModel
class DoctorDashboardViewmodel @Inject constructor(private val repository: DoctorDashboardRepository) :
    ViewModel() {

    private var doctorID:Int = -1
    private val _transactionData: MutableLiveData<Resource<List<DoctorDashboardTxData>>> =
        MutableLiveData(Resource.Loading())
    val transactionData: LiveData<Resource<List<DoctorDashboardTxData>>> = _transactionData

    private val _dateRange: MutableLiveData<String> = MutableLiveData("")
    val dateRange: LiveData<String> = _dateRange
    private var dateRangePair: Pair<Long, Long> =
        Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(),
            MaterialDatePicker.todayInUtcMilliseconds())

    private var cityData:List<MdrCity> = listOf()
    private val selectedCityIndices:MutableSet<Int> = mutableSetOf()
    val cityFilterText = MutableLiveData("City")
    var selectedCityData: BooleanArray = booleanArrayOf()
    private var cityNames:List<String> = listOf()

    private var divisionData:List<MdrCategory> = listOf()
    var divisionNames:List<String> = listOf()
    private val selectedDivisionIndices:MutableSet<Int> = mutableSetOf()
    val divisionFilterText = MutableLiveData("Division")
    var selectedDivisionData:BooleanArray = booleanArrayOf()

    init {
        getAllCities()
        getAllDivisions()
        setDefaultDateRange()
    }

    private val _totalAmountPair:MutableLiveData<Pair<String,String>> = MutableLiveData(Pair(TextUtils.getCurrencyFormat(0.0),TextUtils.getCurrencyFormat(0.0)))
    val totalAmountPair:LiveData<Pair<String,String>> = _totalAmountPair

    private val baseQuery:String = "SELECT `S`.service_id as transactionId, " +
            "`S`.transaction_type as transactionType, " +
            "`S`.service_date as transactionDate, " +
            "`S`.service_amount as transactionAmount, " +
            "`D`.category_name as divisionName, " +
            "`C`.city_name as cityName, " +
            "`S`.created_on as createdOn, " +
            "`S`.updated_on as updatedOn" +
            " FROM MDR_SERVICE `S` " +
            "LEFT JOIN MDR_CATEGORY `D` " +
            "ON (`D`.category_id == `S`.category_id) " +
            "LEFT JOIN MDR_CITY `C` " +
            "ON (`C`.city_id == `S`.city_id) WHERE "

    private val doctorDashboardEventsChannel: Channel<doctorDashboardEvents> = Channel()
    val doctorDashboardEvent = doctorDashboardEventsChannel.receiveAsFlow()

    private val txTypeFilter:MutableSet<TransactionType> = mutableSetOf()
    val txAmountFilter:MutableLiveData<String> = MutableLiveData("")

    private fun setDefaultDateRange() {
        _dateRange.value =
            "From ${DateUtils.fromLongToDateString(MaterialDatePicker.thisMonthInUtcMilliseconds())} To ${
                DateUtils.fromLongToDateString(MaterialDatePicker.todayInUtcMilliseconds())
            }"
    }

    fun getTransactionData() = viewModelScope.launch {
        _transactionData.postValue(Resource.Loading())
        repository.getTransactionDataBasedOnFilters(doctorID).collect {
            _transactionData.postValue(Resource.Success(it))
            calculateTotalValue(it)
        }
    }

    private fun calculateTotalValue(data: List<DoctorDashboardTxData>) {
        if (data.isNotEmpty())
        {
            val serivceTotalStr = TextUtils.getCurrencyFormat(data.filter { it.transactionType == TransactionType.SERVICE.name }.sumOf { it.transactionAmount })
            val returnTotalStr = TextUtils.getCurrencyFormat(data.filter { it.transactionType == TransactionType.COLLECTION.name }.sumOf { it.transactionAmount })
            _totalAmountPair.value = Pair(serivceTotalStr,returnTotalStr)
        }
    }

    private fun getAllCities() = viewModelScope.launch {
        repository.getAllCities().collect { cityList ->
            cityData = cityList
            cityNames = cityData.map { it.cityName }
            selectedCityData = BooleanArray(cityList.size)
        }
    }

    private fun getAllDivisions() = viewModelScope.launch {
        repository.getAllCategories().collect { divisionList ->
            divisionData = divisionList
            selectedDivisionData = BooleanArray(divisionList.size)
            divisionNames = divisionList.map { it.categoryName }
        }
    }

    private val _doctorData: MutableLiveData<Resource<HomeScreenDoctorData>> =
        MutableLiveData(Resource.Loading())
    val doctorData: LiveData<Resource<HomeScreenDoctorData>> = _doctorData

    fun getDoctorDataById() = viewModelScope.launch {
        _doctorData.postValue(Resource.Loading())
        repository.getDoctorData(doctorID).collect {
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
            dateRangePair = it
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
                builder.append(cityData[i].cityName)
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
        selectedCityData = BooleanArray(cityData.size) { false }
        cityFilterText.value = "City"
    }

    fun addDivision(index: Int) {
        selectedDivisionIndices.add(index)
    }

    fun removeDivision(index: Int)
    {
        selectedDivisionIndices.remove(index)
    }

    fun showDivisionSelection() = viewModelScope.launch {
        if (selectedDivisionIndices.isNotEmpty())
        {
            val builder = StringBuilder()
            selectedDivisionIndices.forEachIndexed { index, i ->
                builder.append(divisionData[i].categoryName)
                if (index != selectedDivisionIndices.size - 1) {
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

    fun updateTxTypeFilter(checkedIds: List<Int>) {
        txTypeFilter.clear()
        if (checkedIds.isNotEmpty()) {
            checkedIds.forEach {
                if (it == R.id.chip_service) {
                    txTypeFilter.add(TransactionType.SERVICE)
                } else if (it == R.id.chip_return) {
                    txTypeFilter.add(TransactionType.COLLECTION)
                }
            }
        }
    }

    fun getTransactionDataWithFilters(doctorId: Int)
    {
        viewModelScope.launch {
            val query = constructQuery(doctorId)
            repository.getDoctorDatax(SimpleSQLiteQuery(query))
                .collect{
                    _transactionData.postValue(Resource.Success(it))
                    calculateTotalValue(it)
            }
        }
    }

    fun onClearButtonClicked() = viewModelScope.launch {
        doctorDashboardEventsChannel.send(doctorDashboardEvents.clearChipSelection)
        dateRangePair = Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(),
            MaterialDatePicker.todayInUtcMilliseconds())
        cityFilterText.postValue("City")
        divisionFilterText.postValue("Division")
        clearCitySelection()
        clearDivisionSelection()
        clearTxTypeFilter()
        txAmountFilter.postValue("")
        getTransactionData()
    }

    private fun clearTxTypeFilter() {
        txTypeFilter.clear()
    }

    fun constructQuery(doctorId: Int):String
    {
        val queryBuilder = StringBuilder()
        queryBuilder.append(baseQuery)
        queryBuilder.append("`S`.serviced_doctor_id = $doctorId AND ")

            if (dateRangePair.first != null)
            {
                queryBuilder.append("`S`.service_date >= ${dateRangePair.first} AND ")
            }
            if (dateRangePair.second != null)
            {
                queryBuilder.append("`S`.service_date <= ${dateRangePair.second} AND ")
            }

        if (txTypeFilter.isEmpty())
        {
            queryBuilder.append(" 1=1 AND ")
        }
        else if (txTypeFilter.size == 2)
        {
            queryBuilder.append(" 1=1 AND ")
        }
        else
        {
            if (txTypeFilter.contains(TransactionType.SERVICE))
            {
                queryBuilder.append(" `S`.transaction_type = 'SERVICE' AND ")
            }
            else
            {
                queryBuilder.append(" `S`.transaction_type = 'COLLECTION' AND ")
            }
        }

        if (!txAmountFilter.value.isNullOrEmpty())
        {
            queryBuilder.append("`S`.service_amount = ${txAmountFilter.value!!.toInt()} AND ")
        }
        else
        {
            queryBuilder.append(" 1=1 AND ")
        }

        if (selectedCityIndices.isEmpty())
        {
            queryBuilder.append(" 1=1 AND ")
        }
        else
        {
            val cityIds = selectedCityIndices.map { cityData[it].cityId.toString() }.joinToString { it }
            queryBuilder.append(" `C`.city_id in ($cityIds) AND ")
        }
        if (selectedDivisionIndices.isEmpty())
        {
            queryBuilder.append(" 1=1 ")
        }
        else
        {
            val divisionIds = selectedDivisionIndices.map { divisionData[it].categoryID.toString() }.joinToString { it }
            queryBuilder.append("`D`.category_id in ($divisionIds) ")
        }

        return queryBuilder.toString()
    }

    fun getCityNames():List<String> = cityNames

    fun setDoctorId(id:Int)
    {
        doctorID = id
    }
}

sealed class doctorDashboardEvents {
    object addNewServiceClicked : doctorDashboardEvents()
    object addNewCollectionClicked : doctorDashboardEvents()
    object showDateRangePicker : doctorDashboardEvents()
    object clearChipSelection: doctorDashboardEvents()
}
