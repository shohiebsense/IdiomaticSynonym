package com.shohiebsense.idiomaticsynonym.view.custom

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.shohiebsense.idiomaticsynonym.R
import kotlinx.android.synthetic.main.fragment_dialog_input_document_page.view.*


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
        builder.setTitle(R.string.dialog_title_find_idioms)
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