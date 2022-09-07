package com.geekydroid.managedr.ui.dataExport

import com.geekydroid.managedr.ui.settings.model.ExportDoctorData
import com.geekydroid.managedr.utils.TextUtils
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row

object DataExport {


    suspend fun createWorkBook(data: List<ExportDoctorData>, cityNames: List<String>) : HSSFWorkbook {
        val workBook = HSSFWorkbook()
        val sheets = createSheetsForEachCity(cityNames, workBook)
        sheets.forEach { sheet ->
            writePerDoctorData(sheet, data.filter { it.cityName == sheet.sheetName })
        }
        return workBook
    }

    private suspend fun writePerDoctorData(
        sheet: HSSFSheet,
        data: List<ExportDoctorData>,
    ) {
        val perDoctorData = data.groupBy { it.doctorName }
        var rowNumber = 0
        perDoctorData.forEach {
            writeDoctorData(rowNumber, it.key, it.value, sheet)
            rowNumber += it.value.size + 5
        }
    }

    private suspend fun writeDoctorData(
        startRowNumber: Int,
        doctorName: String,
        data: List<ExportDoctorData>,
        sheet: HSSFSheet,
    ) {
        var rowNumber = startRowNumber
        val totalServiceAmount = data.filter { it.transactionType == "SERVICE" }.sumOf { serviceList -> serviceList.transactionAmount }
        val totalReturnAmount = data.filter { it.transactionType == "COLLECTION" }.sumOf { collectionList -> collectionList.transactionAmount }
        writeDoctorName(rowNumber++, sheet, doctorName)
        writeSheetHeaders(rowNumber++, sheet, listOf("S.No",
            "Date of Transaction",
            "Transaction Type",
            "City",
            "Category",
            "Amount"))
        writeDoctorTransactions(rowNumber,sheet,data)
        rowNumber = data.size+1
        writeDoctorTransactionTotals(totalServiceAmount,totalReturnAmount,rowNumber,sheet)
    }

    private fun writeDoctorTransactionTotals(
        totalServiceAmount: Double,
        totalReturnAmount: Double,
        startRowNumber: Int,
        sheet: HSSFSheet,
    ) {
        var rowNumber = startRowNumber
        var row = sheet.createRow(rowNumber++)
        createCell(row,4,"Total Service Amount")
        createCell(row,5,TextUtils.getCurrencyFormat(totalServiceAmount))
        row = sheet.createRow(rowNumber)
        createCell(row,4,"Total Return Amount")
        createCell(row,5,TextUtils.getCurrencyFormat(totalReturnAmount))
    }

    private suspend fun writeDoctorTransactions(
        rowNumber: Int,
        sheet: HSSFSheet,
        data: List<ExportDoctorData>,
    ) {
        var rowIndex = rowNumber
        var serialNo = 1
        data.forEach { transaction ->
            val row = sheet.createRow(rowIndex)
            var columnNumber = 0
            createCell(row,columnNumber++,(serialNo++).toString())
            createCell(row,columnNumber++,transaction.transactionDateFormatted)
            createCell(row,columnNumber++,transaction.transactionType)
            createCell(row,columnNumber++,transaction.cityName)
            createCell(row,columnNumber++,transaction.divisionName)
            createCell(row,columnNumber,transaction.transactionAmountFormatted)
            rowIndex++
        }
    }

    private fun writeSheetHeaders(rowNumber: Int, sheet: HSSFSheet, headerNames: List<String>) {
        val row = sheet.createRow(rowNumber)
        val columnWidth = 15*500
        headerNames.forEachIndexed{ index,value ->
            sheet.setColumnWidth(index,columnWidth)
            val cell = row.createCell(index)
            cell.setCellValue(value)
        }
    }

    private fun createCell(row:Row, columnNumber: Int, value: String) {
        val cell = row.createCell(columnNumber)
        cell.setCellValue(value)
    }

    private suspend fun writeDoctorName(startRowNumber: Int, sheet: HSSFSheet, doctorName: String) {
        val row = sheet.createRow(startRowNumber)
        val cell = row.createCell(0)
        cell.setCellValue(doctorName)

    }

    private suspend fun createSheetsForEachCity(cityNames: List<String>, workbook: HSSFWorkbook): List<HSSFSheet> {
        val sheetList: MutableList<HSSFSheet> = mutableListOf()

        cityNames.forEach { city ->
            sheetList.add(workbook.createSheet(city))
        }

        return sheetList
    }

}