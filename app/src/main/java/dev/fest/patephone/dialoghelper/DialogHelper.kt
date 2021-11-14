package dev.fest.patephone.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import dev.fest.patephone.activity.MainActivity
import dev.fest.patephone.accounthelper.AccountHelper
import dev.fest.patephone.databinding.SignDialogBinding
import dev.fest.patephone.R


class DialogHelper(val activity: MainActivity) {
    val accountHelper = AccountHelper(activity)
    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(activity)
        val bindingDialogHelper = SignDialogBinding.inflate(activity.layoutInflater)
        builder.setView(bindingDialogHelper.root)
        setDialogState(index, bindingDialogHelper)

        val dialog = builder.create()
        bindingDialogHelper.buttonSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, bindingDialogHelper, dialog)
        }
        bindingDialogHelper.buttonForgetPassword.setOnClickListener {
            setOnClickResetPassword(bindingDialogHelper, dialog)
        }
        bindingDialogHelper.buttonSignInGoogle.setOnClickListener {
            accountHelper.signInClientWithGoogle()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(
        bindingDialogHelper: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        if (bindingDialogHelper.editTextSignEmail.text.isNotEmpty()) {
            activity.mAuth.sendPasswordResetEmail(bindingDialogHelper.editTextSignEmail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            activity,
                            R.string.email_reset_password_was_sent,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            dialog?.dismiss()
        } else {
            bindingDialogHelper.textViewDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSignUpIn(
        index: Int,
        bindingDialogHelper: SignDialogBinding,
        dialog: AlertDialog?
    ) {
        dialog?.dismiss()
        if (index == DialogConst.SIGN_UP_STATE) {
            accountHelper.signUpWithEmail(
                bindingDialogHelper.editTextSignEmail.text.toString(),
                bindingDialogHelper.editTextSignPassword.text.toString(),
            )
        } else {
            accountHelper.signInWithEmail(
                bindingDialogHelper.editTextSignEmail.text.toString(),
                bindingDialogHelper.editTextSignPassword.text.toString()
            )
        }
    }

    private fun setDialogState(index: Int, bindingDialogHelper: SignDialogBinding) {
        if (index == DialogConst.SIGN_UP_STATE) {
            bindingDialogHelper.apply {
                textViewSignTitle.text =
                    activity.resources.getString(R.string.ad_sign_up)
                buttonSignUpIn.text =
                    activity.resources.getString(R.string.sign_up_action)
            }
        } else {
            bindingDialogHelper.apply {
                textViewSignTitle.text =
                    activity.resources.getString(R.string.ad_sign_in)
                buttonSignUpIn.text =
                    activity.resources.getString(R.string.sign_in_action)
                buttonForgetPassword.visibility = View.VISIBLE
            }
        }
    }
}