package com.geekydroid.managedr.ui.settings.model

import com.geekydroid.managedr.application.ScreenData

data class SettingsEditData(
    val id: Int = 0,
    val name: String = "",
    val createdOn: Long = 0L,
    val updatedOn: Long = 0L,
    val type:String = ""
) : ScreenData()