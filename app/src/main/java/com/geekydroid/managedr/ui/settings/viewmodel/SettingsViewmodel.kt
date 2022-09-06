package com.geekydroid.managedr.ui.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geekydroid.managedr.ManageDrApp
import com.geekydroid.managedr.ui.settings.model.ExportDoctorData
import com.geekydroid.managedr.ui.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.apache.poi.hssf.usermodel.HSSFRow
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import javax.inject.Inject


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
                val workBook = createWorkBook(data,cityNames)
                createExcel(application,workBook,uri)
            }
        }
    }

    private fun createExcel(application: ManageDrApp, workBook: HSSFWorkbook, uri: Uri?) {
        try {
            val fos = application.contentResolver.openOutputStream(uri!!)
            workBook.write(fos)
            fos!!.close()
        }
        catch (throwable:Throwable)
        {

        }
    }

    private fun createWorkBook(data: List<ExportDoctorData>, cityNames: List<String>): HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheets = createSheets(workBook,cityNames)
        sheets.forEach { sheet ->
            createSheetHeaders(sheet,
                listOf("S.No",
                    "Date of Transaction",
                    "Transaction Type",
                    "City",
                    "Category",
                    "Amount"))
        }
        for ((index,sheet) in sheets.withIndex())
        {
            writeDoctorDataByCity(sheet,data.filter { it.cityName == cityNames[index] })
        }
        return workBook
    }

    private fun writeDoctorDataByCity(
        sheet: HSSFSheet,
        data: List<ExportDoctorData>
    ) {

        if (data.isNotEmpty())
        {
            data.forEachIndexed{index, doctorData ->
                writeDoctorData((index+1).toString(),index+1,sheet,doctorData)
            }
        }
    }

    private fun writeDoctorData(serialNo:String,index: Int, sheet: HSSFSheet, doctorData: ExportDoctorData) {
        val row = sheet.createRow(index)
        var columnIndex = 0
        createCell(row,columnIndex++,serialNo)
        createCell(row,columnIndex++,doctorData.transactionDateFormatted)
        createCell(row,columnIndex++,doctorData.transactionType)
        createCell(row,columnIndex++,doctorData.cityName)
        createCell(row,columnIndex++,doctorData.divisionName)
        createCell(row,columnIndex,doctorData.transactionAmountFormatted)
    }

    private fun createCell(row: HSSFRow, columnIndex: Int, value: String) {
        val cell = row.createCell(columnIndex)
        cell?.setCellValue(value)
    }

    private fun createSheetHeaders(sheet: Sheet, headers: List<String>) {
        val row = sheet.createRow(0)
        for ((index,value) in headers.withIndex())
        {
            val columnWidth = 15*500
            sheet.setColumnWidth(index,columnWidth)
            val cell = row.createCell(index)
            cell?.setCellValue(value)
        }
    }

    private fun createSheets(workbook:HSSFWorkbook,cityNames: List<String>): List<HSSFSheet> {

        val sheetList:MutableList<HSSFSheet> = mutableListOf()
        cityNames.forEach{ city ->
            sheetList.add(workbook.createSheet(city))
        }
        return sheetList
    }

}

sealed class SettingsEvents
{
    object openFilePicker : SettingsEvents()
}