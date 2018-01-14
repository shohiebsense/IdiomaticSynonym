package com.shohiebsense.idiomaticsynonym.view.custom

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.shohiebsense.idiomaticsynonym.R
import kotlinx.android.synthetic.main.fragment_dialog_input_find_idioms.view.*

/**
 * Created by Shohiebsense on 23/12/2017.
 */
class FindIdiomsDialogFragment : DialogFragment() {

    interface InputDialogListener{
        fun onPositiveClick()
    }

    lateinit var  listener : InputDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_message_input_page)
        listener = targetFragment as InputDialogListener
        var view = activity.layoutInflater.inflate(R.layout.fragment_dialog_input_find_idioms, null)

        view.okButton.setOnClickListener({
            dismiss()
        })

        builder.setView(view)
        // inputNumberOfPagesEditText.filters = arrayOf(InputFilterPageMax())
        builder.setMessage(R.string.dialog_message_input_page)
        return builder.create()
    }


}