package com.geekydroid.managedr.utils

import com.geekydroid.managedr.application.ScreenData

interface UiOnClickListener {

    fun onClick(position:Int = -1,data:ScreenData? = null)
}