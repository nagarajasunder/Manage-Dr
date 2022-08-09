package com.geekydroid.managedr.providers

import java.lang.Exception
import kotlin.Result

sealed class Resource<T>
{
    class Loading<T>(val message:String? = null) : Resource<T>()
    class Success<T>(val data:T? = null) : Resource<T>()
    class Error<T>(val exception: Throwable? = null,val errorMessage:String? = null) : Resource<T>()
}
