package com.geekydroid.managedr.ui.settings.viewmodel

import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ManageDrApp
import com.geekydroid.managedr.ui.dataExport.DataExport
import com.geekydroid.managedr.ui.settings.model.SettingsEditType
import com.geekydroid.managedr.ui.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import javax.inject.Inject

private const val TAG = "SettingsViewmodel"
@HiltViewModel
class SettingsViewmodel @Inject constructor(
    private val repository: SettingsRepository
    ) : ViewModel() {

    private val eventChannel: Channel<SettingsEvents> = Channel()
    val events:Flow<SettingsEvents> = eventChannel.receiveAsFlow()

    fun exportDataClicked() = viewModelScope.launch {
        eventChannel.send(SettingsEvents.openFilePicker)
    }

    fun exportData(uri: Uri?) {
        viewModelScope.launch {
            val count = repository.getTransactionCount()
            if (count > 0)
            {
                eventChannel.send(SettingsEvents.exportDataToExcel(uri!!))
            }
            else
            {
                eventChannel.send(SettingsEvents.showNoTransactionError)
            }
        }
    }

    fun settingsEditableClicked() {
        viewModelScope.launch {
            eventChannel.send(SettingsEvents.openCityDivisionScreen(SettingsEditType.EDIT_TYPE_DIVISION))
        }
    }


}

sealed class SettingsEvents
{
    object openFilePicker : SettingsEvents()
    data class exportDataToExcel(val uri: Uri):SettingsEvents()
    object showNoTransactionError : SettingsEvents()
    data class openCityDivisionScreen(val editType:SettingsEditType) : SettingsEvents()
}