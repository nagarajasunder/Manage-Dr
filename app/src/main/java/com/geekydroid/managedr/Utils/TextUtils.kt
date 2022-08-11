package com.geekydroid.managedr.Utils

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