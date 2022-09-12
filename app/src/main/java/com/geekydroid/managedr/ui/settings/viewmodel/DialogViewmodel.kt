package com.geekydroid.managedr.ui.settings.viewmodel

import android.app.Dialog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DialogViewmodel @Inject constructor() : ViewModel(){

    val editableData:MutableLiveData<String> = MutableLiveData("")
    private val eventsChannel: Channel<DialogEvents> = Channel()
    val events:Flow<DialogEvents> = eventsChannel.receiveAsFlow()

    fun validateInput()
    {
        viewModelScope.launch {
            if (editableData.value.isNullOrEmpty() || editableData.value!!.length > 20)
            {
                eventsChannel.send(DialogEvents.emptyInputError)
            }
            else
            {
                eventsChannel.send(DialogEvents.checkForDuplicateInput(editableData.value!!))
            }
        }
    }

    fun updateInputType(type:String)
    {
        editableData.value = type
    }

    fun onCloseButtonClicked()
    {
        viewModelScope.launch {
            eventsChannel.send(DialogEvents.dismissDialog)
        }
    }
}

sealed class DialogEvents
{
    object emptyInputError : DialogEvents()
    object dismissDialog : DialogEvents()
    data class checkForDuplicateInput(val input:String) : DialogEvents()
}