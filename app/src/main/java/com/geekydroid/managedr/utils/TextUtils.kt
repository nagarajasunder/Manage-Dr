package com.geekydroid.managedr.utils

object TextUtils {

    fun trimText(text:String? = null) : String
    {
        if (!text.isNullOrEmpty())
        {
            return text.trim()
        }
        return ""
    }
}