package com.geekydroid.managedr.ui.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ManageDrApp
import com.geekydroid.managedr.ui.dataExport.DataExport
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
class SettingsViewmodel @Inject constructor(private val repository: SettingsRepository,private val application:ManageDrApp) : ViewModel() {

    private val eventChannel: Channel<SettingsEvents> = Channel()
    val events:Flow<SettingsEvents> = eventChannel.receiveAsFlow()

    fun exportDataClicked() = viewModelScope.launch {
        eventChannel.send(SettingsEvents.openFilePicker)
    }

    fun exportData(uri: Uri?) {
        viewModelScope.launch {
            val data = repository.getDataForExport()
            val cityNames = repository.getCityNames()
            if (data.isNotEmpty() && cityNames.isNotEmpty())
            {
                val workBook = DataExport.createWorkBook(data,cityNames)
                createExcel(application,workBook,uri)
            }
        }
    }

    private fun createExcel(application: ManageDrApp, workBook: XSSFWorkbook, uri: Uri?) {
        try {
            val fos = application.contentResolver.openOutputStream(uri!!)
            workBook.write(fos)
            fos!!.close()
        }
        catch (throwable:Throwable)
        {

        }
    }

}

sealed class SettingsEvents
{
    object openFilePicker : SettingsEvents()
}