package com.shohiebsense.idiomaticsynonym.view.custom

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import kotlinx.android.synthetic.main.fragment_dialog_input_document_page.view.*


/**
 * Created by Shohiebsense on 09/11/2017.
 */
class InputDocumentPageDialogFragment : DialogFragment(), TextWatcher {


    companion object {
        val PAGE_COUNT = "pagecount"

        fun newInstance(pageCount : Int) : InputDocumentPageDialogFragment{
            val fragment = InputDocumentPageDialogFragment()
            var args = Bundle()
            args.putInt(PAGE_COUNT,pageCount)
            fragment.arguments = args
            return fragment
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }




    interface InputDialogListener {
        fun performFetchingText(from : Int, to:Int)
    }

    lateinit var listener : InputDialogListener
    var numberOfPage = Int
    lateinit var fromEditText : EditText
    lateinit var toEditText: EditText
    lateinit var okButton : Button
    var pageCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.SherifDialogTheme)
        if (arguments != null) {
            pageCount = arguments!!.getInt(InputDocumentPageDialogFragment.PAGE_COUNT)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        AppUtil.makeErrorLog("engga ke sini ???/")
        if( fromEditText.text.toString().isNotBlank() && toEditText.text.toString().isNotBlank()){
            var numberFrom = fromEditText.text.toString().toInt()
            var numberTo = toEditText.text.toString().toInt()
            var validEqual = ((numberTo - numberFrom) >= 0 && (numberTo <= pageCount))
            AppUtil.makeErrorLog("haiii , not here??? "+validEqual + " "+pageCount)
            okButton.isEnabled = validEqual
        }
        else{
            okButton.isEnabled = false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder = AlertDialog.Builder(activity)
        builder.setTitle(R.string.dialog_title_find_idioms)
        listener = targetFragment as InputDialogListener
        var view = activity!!.layoutInflater.inflate(R.layout.fragment_dialog_input_document_page, null)
        fromEditText = view.inputNumberFromEditText
        toEditText = view.inputNumberToEditText
        okButton = view.okButton
        fromEditText.addTextChangedListener(this)
        toEditText.addTextChangedListener(this)
        view.limitNumberPageTextView.append("\n"+getString(R.string.number_of_pages) + ":"+pageCount)
        view.okButton.setOnClickListener{
            var from = fromEditText.text.toString().toInt()
            var to = toEditText.text.toString().toInt()
            var number = to - from
            listener.performFetchingText(from,to)
            dismiss()
        }

        builder.setView(view)
        // inputNumberOfPagesEditText.filters = arrayOf(InputFilterPageMax())

        builder.setMessage(R.string.dialog_message_input_page)
        return builder.create()
    }


}