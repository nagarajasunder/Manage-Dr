package com.geekydroid.managedr.utils

import java.text.NumberFormat
import java.util.*

object TextUtils {

    private val indLocale = Locale("en","IN")
    private val currency:Currency = Currency.getInstance(indLocale)
    private val numberFormat = NumberFormat.getNumberInstance(indLocale)

    fun trimText(text:String? = null) : String
    {
        if (!text.isNullOrEmpty())
        {
            return text.trim()
        }
        return ""
    }

    fun getCurrencyFormat(amt:Double) = "${currency.symbol}${numberFormat.format(amt)}"


}