package dev.fest.patephone.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import dev.fest.patephone.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(activity: Activity): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val bindingDialogHelper = ProgressDialogLayoutBinding.inflate(activity.layoutInflater)
        builder.setView(bindingDialogHelper.root)
        val dialog = builder.create()
        //запрет на закрытие диалога
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}