package com.geekydroid.managedr.utils

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

fun DialogFragment.setWidthPercent(percentage:Int)
{
    val percent = percentage.toFloat()/100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0,0,widthPixels,heightPixels) }
    val percentWidth = rect.width()*percent
    dialog?.window?.setLayout(percentWidth.toInt(),ViewGroup.LayoutParams.WRAP_CONTENT)
}

fun createAlertDialog(context: Context,isCancelable:Boolean,title:String,message:String,positiveCtaText:String,negativeCtaText:String,callback: dialogCallback)
{
    val deleteAlertDialog = AlertDialog.Builder(context)
    deleteAlertDialog.setCancelable(isCancelable)
    deleteAlertDialog.setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveCtaText) { dialog, _ ->
            callback.onPositiveButtonClick(dialog)
        }
        .setNegativeButton(negativeCtaText
        ) { dialog, _ ->
            callback.onNegativeButtonOnClick(dialog)
        }.show()
}

interface dialogCallback
{
    fun onPositiveButtonClick(dialog: DialogInterface)
    fun onNegativeButtonOnClick(dialog: DialogInterface)
}
