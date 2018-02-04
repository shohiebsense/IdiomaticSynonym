package com.shohiebsense.idiomaticsynonym.view.custom

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.shohiebsense.idiomaticsynonym.R
import kotlinx.android.synthetic.main.fragment_dialog_empty_result.view.*

/**
 * Created by Shohiebsense on 04/02/2018.
 */
class EmptyResultDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.SherifDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_dialog_empty_result, null)
        view.okButton.setOnClickListener({
            dismiss()
        })
        builder.setView(view)
        // inputNumberOfPagesEditText.filters = arrayOf(InputFilterPageMax())
        builder.setMessage(R.string.dialog_message_result_empty)
        return builder.create()
    }

}