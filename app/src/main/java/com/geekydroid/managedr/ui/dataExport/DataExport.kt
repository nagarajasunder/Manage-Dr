package com.geekydroid.managedr.ui.dataExport

import android.net.Uri
import android.util.Log
import com.geekydroid.managedr.ManageDrApp
import com.geekydroid.managedr.ui.settings.model.ExportDoctorData
import com.geekydroid.managedr.utils.TextUtils
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.RegionUtil
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream

object DataExport {

    private var ROW_NUMBER = 0
    private val EXTRA_LINE_SPACE = 1
    private val MOVE_ROW_TO_NEXT_LINE = 1
    private lateinit var workBook:XSSFWorkbook
    private lateinit var styleMap:MutableMap<String,CellStyle>

    suspend fun createWorkBook(data: List<ExportDoctorData>, cityNames: List<String>,application:ManageDrApp,uri:Uri) {
        try {
            workBook = XSSFWorkbook()
            styleMap = createCellStyles()
            val sheets = createSheetsForEachCity(cityNames, workBook)
            sheets.forEach { sheet ->
                writePerDoctorData(sheet, data.filter { it.cityName == sheet.sheetName })
                ROW_NUMBER = 0
            }
            createExcel(application, workBook,uri)
        }
        catch (throwable:Throwable)
        {

        }
    }

    private fun createExcel(application: ManageDrApp, workBook: XSSFWorkbook, uri: Uri) {
        val fos = application.contentResolver.openOutputStream(uri)
        workBook.write(fos)
        fos!!.close()
    }

    private suspend fun writePerDoctorData(
        sheet: XSSFSheet,
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
        sheet: XSSFSheet,
    ) {
        val totalServiceAmount = data.filter { it.transactionType == "SERVICE" }.sumOf { serviceList -> serviceList.transactionAmount }
        val totalReturnAmount = data.filter { it.transactionType == "COLLECTION" }.sumOf { collectionList -> collectionList.transactionAmount }
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
        sheet: XSSFSheet,
    ) {

        var row = sheet.createRow(ROW_NUMBER)
        createCellWithColor(row,4,"Total Service Amount", styleMap["red_cell_style"]!!,IndexedColors.RED.index)
        createCellWithColor(row,5,TextUtils.getCurrencyFormat(totalServiceAmount),
            styleMap["red_cell_style"]!!,IndexedColors.RED.index)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,4,sheet)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,5,sheet)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        row = sheet.createRow(ROW_NUMBER)
        createCellWithColor(row,4,"Total Return Amount", styleMap["green_cell_style"]!!,IndexedColors.LIGHT_GREEN.index)
        createCellWithColor(row,5,TextUtils.getCurrencyFormat(totalReturnAmount),
            styleMap["green_cell_style"]!!,IndexedColors.LIGHT_GREEN.index)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,4,sheet)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,5,sheet)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        row = sheet.createRow(ROW_NUMBER)
        createCellWithColor(row,4,"Total Transaction Amount",styleMap["green_cell_style"]!!,IndexedColors.LIGHT_GREEN.index)
        createCellWithColor(row,5,TextUtils.getCurrencyFormat((totalServiceAmount+totalReturnAmount)),styleMap["green_cell_style"]!!,IndexedColors.LIGHT_GREEN.index)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,4,sheet)
        createBorder(ROW_NUMBER, ROW_NUMBER,0,5,sheet)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
    }


    private fun createCellWithColor(row: XSSFRow, column: Int, value: String,cellStyle:CellStyle,bgColor:Short) {
        val cell = row.createCell(column)
        cell.setCellValue(value)
        cellStyle.fillForegroundColor = bgColor
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        cell.setCellStyle(cellStyle)
    }

    fun createColoredFontCellStyle(color:Short,fontName:String) : XSSFCellStyle
    {
        val cellStyle = workBook.createCellStyle()
        val font = workBook.createFont()
        font.fontName = fontName
        font.bold = true
        font.color = color
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun writeDoctorTransactions(
        sheet: XSSFSheet,
        data: List<ExportDoctorData>,
    ) {
        var serialNo = 1
        data.forEach { transaction ->
            val row = sheet.createRow(ROW_NUMBER)
            var columnNumber = 0
            createCell(row,columnNumber,(serialNo++).toString())
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            columnNumber++
            createCell(row,columnNumber,transaction.transactionDateFormatted)
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            columnNumber++
            createCell(row,columnNumber,transaction.transactionType)
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            columnNumber++
            createCell(row,columnNumber,transaction.cityName)
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            columnNumber++
            createCell(row,columnNumber,transaction.divisionName)
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            columnNumber++
            createCell(row,columnNumber,transaction.transactionAmountFormatted)
            createBorder(ROW_NUMBER, ROW_NUMBER,columnNumber,columnNumber,sheet)
            ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
        }
        ROW_NUMBER+= EXTRA_LINE_SPACE
        createBorder(ROW_NUMBER, ROW_NUMBER,0,5,sheet)
    }

    private fun writeSheetHeaders(sheet: XSSFSheet, headerNames: List<String>) {
        val row = sheet.createRow(ROW_NUMBER)
        val columnWidth = 15*500
        headerNames.forEachIndexed{ index,value ->
            sheet.setColumnWidth(index,columnWidth)
            val cell = row.createCell(index)
            cell.setCellValue(value)
            createBorder(ROW_NUMBER, ROW_NUMBER,index,index,sheet)
        }
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
    }

    private fun createCell(row:Row, columnNumber: Int, value: String) {
        val cell = row.createCell(columnNumber)
        cell.setCellValue(value)
    }

    private suspend fun writeDoctorName(sheet: XSSFSheet, doctorName: String) {
        val row = sheet.createRow(ROW_NUMBER)
        val cell = row.createCell(0)
        cell.setCellValue(doctorName)
        cell.cellStyle.font.bold = true
        createBorder(ROW_NUMBER, ROW_NUMBER,0,5,sheet)
        ROW_NUMBER+= MOVE_ROW_TO_NEXT_LINE
    }

    private fun createBorder(
        startRow: Int,
        endRow: Int,
        startColumn: Int,
        endColumn: Int,
        sheet: XSSFSheet,
    ) {
        val region = CellRangeAddress(startRow,endRow,startColumn,endColumn)
        RegionUtil.setBorderTop(BorderStyle.THIN,region,sheet)
        RegionUtil.setBorderBottom(BorderStyle.THIN,region,sheet)
        RegionUtil.setBorderLeft(BorderStyle.THIN,region,sheet)
        RegionUtil.setBorderRight(BorderStyle.THIN,region,sheet)
    }

    private suspend fun createSheetsForEachCity(cityNames: List<String>, workbook: XSSFWorkbook): List<XSSFSheet> {
        val sheetList: MutableList<XSSFSheet> = mutableListOf()

        cityNames.forEach { city ->
            sheetList.add(workbook.createSheet(city))
        }

        return sheetList
    }

    private fun createCellStyles(): MutableMap<String, CellStyle> {
        val map = mutableMapOf<String,CellStyle>()
        var cellStyle = workBook.createCellStyle()
        val font = workBook.createFont()
        font.fontName = "red_cell_style"
        font.bold = true
        cellStyle.setFont(font)
        map["red_cell_style"] = cellStyle

        cellStyle = workBook.createCellStyle()
        font.fontName = "green_cell_style"
        cellStyle.setFont(font)
        map["green_cell_style"] = cellStyle

        return map
    }

}