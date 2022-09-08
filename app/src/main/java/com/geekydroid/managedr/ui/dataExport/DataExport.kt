package com.geekydroid.managedr.ui.dataExport

import android.util.Log
import com.geekydroid.managedr.ui.settings.model.ExportDoctorData
import com.geekydroid.managedr.utils.TextUtils
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row

private const val TAG = "DataExport"
object DataExport {

    var ROW_NUMBER = 0
    val EXTRA_LINE_SPACE = 1
    val MOVE_ROW_TO_NEXT_LINE = 1

    suspend fun createWorkBook(data: List<ExportDoctorData>, cityNames: List<String>) : HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheets = createSheetsForEachCity(cityNames, workBook)
        sheets.forEach { sheet ->
            writePerDoctorData(sheet, data.filter { it.cityName == sheet.sheetName })
            ROW_NUMBER = 0
        }
        return workBook
    }

    private suspend fun writePerDoctorData(
        sheet: HSSFSheet,
        data: List<ExportDoctorData>,
    ) {
        val perDoctorData = data.groupBy { it.doctorName }
        perDoctorData.forEach {
            writeDoctorData(it.key, it.value, sheet)
        }
    }

    private suspend fun writeDoctorData(
        doctorName: String,
        data: List<ExportDoctorData>,
        sheet: HSSFSheet,
    ) {
        Log.d(TAG, "writeDoctorData: For city ${sheet.sheetName}")
        Log.d(TAG, "writeDoctorData: doctor data $data")
        val totalServiceAmount = data.filter { it.transactionType == "SERVICE" }.sumOf { serviceList -> serviceList.transactionAmount }
        val totalReturnAmount = data.filter { it.transactionType == "COLLECTION" }.sumOf { collectionList -> collectionList.transactionAmount }
        Log.d(TAG, "writeDoctorData: $totalServiceAmount service amount")
        Log.d(TAG, "writeDoctorData: $totalReturnAmount return amount")
        writeDoctorName(sheet, doctorName)
        writeSheetHeaders(sheet, listOf("S.No",
            "Date of Transaction",
            "Transaction Type",
            "City",
            "Category",
            "Amount"))
        writeDoctorTransactions(sheet,data)
//        ROW_NUMBER += data.size+2
        writeDoctorTransactionTotals(totalServiceAmount,totalReturnAmount,sheet)
    }

    private fun writeDoctorTransactionTotals(
        totalServiceAmount: Double,
        totalReturnAmount: Double,
        sheet: HSSFSheet,
    ) {
        var row = sheet.createRow(ROW_NUMBER)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        createCell(row,4,"Total Service Amount")
        createCell(row,5,TextUtils.getCurrencyFormat(totalServiceAmount))
        row = sheet.createRow(ROW_NUMBER)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        createCell(row,4,"Total Return Amount")
        createCell(row,5,TextUtils.getCurrencyFormat(totalReturnAmount))
        row = sheet.createRow(ROW_NUMBER)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        createCell(row,4,"Total Transaction Amount")
        createCell(row,5,TextUtils.getCurrencyFormat((totalServiceAmount+totalReturnAmount)))
    }

    private fun writeDoctorTransactions(
        sheet: HSSFSheet,
        data: List<ExportDoctorData>,
    ) {
        var serialNo = 1
        data.forEach { transaction ->
            val row = sheet.createRow(ROW_NUMBER)
            var columnNumber = 0
            createCell(row,columnNumber++,(serialNo++).toString())
            createCell(row,columnNumber++,transaction.transactionDateFormatted)
            createCell(row,columnNumber++,transaction.transactionType)
            createCell(row,columnNumber++,transaction.cityName)
            createCell(row,columnNumber++,transaction.divisionName)
            createCell(row,columnNumber,transaction.transactionAmountFormatted)
            ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        }
        ROW_NUMBER+= EXTRA_LINE_SPACE
    }

    private fun writeSheetHeaders(sheet: HSSFSheet, headerNames: List<String>) {
        val row = sheet.createRow(ROW_NUMBER)
        val columnWidth = 15*500
        headerNames.forEachIndexed{ index,value ->
            sheet.setColumnWidth(index,columnWidth)
            val cell = row.createCell(index)
            cell.setCellValue(value)
        }
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
    }

    private fun createCell(row:Row, columnNumber: Int, value: String) {
        val cell = row.createCell(columnNumber)
        cell.setCellValue(value)
    }

    private suspend fun writeDoctorName(sheet: HSSFSheet, doctorName: String) {
        val row = sheet.createRow(ROW_NUMBER)
        val cell = row.createCell(0)
        cell.setCellValue(doctorName)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
    }

    private suspend fun createSheetsForEachCity(cityNames: List<String>, workbook: HSSFWorkbook): List<HSSFSheet> {
        val sheetList: MutableList<HSSFSheet> = mutableListOf()

        cityNames.forEach { city ->
            sheetList.add(workbook.createSheet(city))
        }

        return sheetList
    }

}