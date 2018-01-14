package com.shohiebsense.idiomaticsynonym.view

import android.text.InputFilter
import android.text.Spanned

/**
 * Created by Shohiebsense on 09/11/2017.
 */

class InputFilterPageMax : InputFilter {

    //1 - 14 number of pages allowed

    override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
        var input = Integer.parseInt(dest.toString() + source.toString())
        if(isInRange(1,14,input)){
            return input.toString()
        }
        return ""
    }

    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c >= a && c <= b else c >= b && c <= a
    }
}