package com.geekydroid.managedr.ui.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.providers.Resource
import com.geekydroid.managedr.ui.settings.model.SettingsEditData
import com.geekydroid.managedr.ui.settings.model.SettingsEditType
import com.geekydroid.managedr.ui.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CityDivisionViewModel"
@HiltViewModel
class CityDivisionViewModel @Inject constructor(private val repository:SettingsRepository) : ViewModel() {

    private val _editData: MutableLiveData<Resource<List<SettingsEditData>>> =
        MutableLiveData(Resource.Loading())
    val editData: LiveData<Resource<List<SettingsEditData>>> = _editData
    private val selectedType = MutableStateFlow(SettingsEditType.EDIT_TYPE_CITY.name)
    private val selectedTypeLiveData = MutableLiveData(SettingsEditType.EDIT_TYPE_CITY.name)

    init {
        getData()
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

    fun updateEditType(editTypeCity: SettingsEditType) {
        selectedType.value = editTypeCity.name
    }

}