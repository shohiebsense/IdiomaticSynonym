package com.shohiebsense.idiomaticsynonym.view.custom

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.text.Editable
import android.text.TextWatcher
import android.os.Bundle
import com.shohiebsense.idiomaticsynonym.view.activity.createfile.CreateFileActivity
import com.shohiebsense.idiomaticsynonym.R
import kotlinx.android.synthetic.main.fragment_dialog_create_file.view.*


/**
 * Created by Shohiebsense on 24/06/2018
 */

class CreateFileDialogFragment : DialogFragment()  {

    private var mListener: CreateFileDialogFragment.DialogListener? = null

    companion object {

        fun newInstance(): CreateFileDialogFragment {

            return CreateFileDialogFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(getActivity())

        val view =  activity!!.layoutInflater.inflate(R.layout.fragment_dialog_create_file, null)

        // if text is empty, disable the dialog positive button
        val nameEditText = view.name

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                (getDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(editable.length > 0)
            }
        })



        builder.setTitle(R.string.new_file)
        builder.setView(view)
        builder.setPositiveButton(R.string.label_save, DialogInterface.OnClickListener { dialogInterface, i -> mListener!!.onNewFile(nameEditText.text.toString()) })

        val dialog = builder.create()
        view.post(Runnable { dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false) })
        dialog.setCancelable(false)
        return dialog
    }

    interface DialogListener {
        fun onNewFile(name: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mListener = (context as CreateFileActivity) as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement DialogListener")
        }

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }




}