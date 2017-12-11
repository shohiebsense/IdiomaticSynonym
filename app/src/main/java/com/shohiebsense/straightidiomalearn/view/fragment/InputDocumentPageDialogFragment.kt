package com.shohiebsense.straightidiomalearn.view.fragment

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.view.InputFilterPageMax
import kotlinx.android.synthetic.main.fragment_dialog_input_document_page.*
import com.shohiebsense.straightidiomalearn.R.id.inputNumberOfPagesEditText
import kotlinx.android.synthetic.main.fragment_dialog_input_document_page.view.*
import kotlinx.android.synthetic.main.fragment_read.*


/**
 * Created by Shohiebsense on 09/11/2017.
 */
class InputDocumentPageDialogFragment : DialogFragment() {


    interface InputDialogListener {
        fun onDialogPositiveClick(number : Int)
    }

    lateinit var listener : InputDialogListener
    var numberOfPage = Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_message_input_page)
        listener = targetFragment as InputDialogListener
        var view = activity.layoutInflater.inflate(R.layout.fragment_dialog_input_document_page, null)

        view.okButton.setOnClickListener({
            if(!view.inputNumberOfPagesEditText.text.isEmpty() && !view.inputNumberOfPagesEditText.text.isNullOrBlank()) {


            var number = view.inputNumberOfPagesEditText.text.toString().toInt()

            if(number <= 14){
                listener.onDialogPositiveClick(number)
                dismiss()
            }
            else{
                Snackbar.make(view, "Max Page is 14", Snackbar.LENGTH_LONG).show()
            }
            }
            else{
                Snackbar.make(view, "Can't leave empty", Snackbar.LENGTH_LONG).show()
            }
        })

        builder.setView(view)
        // inputNumberOfPagesEditText.filters = arrayOf(InputFilterPageMax())
        builder.setMessage(R.string.dialog_message_input_page)
        return builder.create()
    }


}