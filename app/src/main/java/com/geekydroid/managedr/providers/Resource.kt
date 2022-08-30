package com.geekydroid.managedr.providers

import java.lang.Exception
import kotlin.Result

sealed class Resource<T>(data:T? = null,message:String? = null,exception: Throwable? = null)
{
    class Loading<T>(message:String? = null) : Resource<T>(message = message)
    class Success<T>(val data:T? = null) : Resource<T>(data = data)
    class Error<T>(val exception: Throwable? = null,val errorMessage:String? = null) : Resource<T>(exception = exception, message = errorMessage)
}
