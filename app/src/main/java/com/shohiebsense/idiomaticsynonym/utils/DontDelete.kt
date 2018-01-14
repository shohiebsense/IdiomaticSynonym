package com.shohiebsense.idiomaticsynonym.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.shohiebsense.idiomaticsynonym.R


/**
 * Created by Shohiebsense on 21/09/2017.
 */

class DontDelete {


    internal fun anu() {
        val message = "HP E2B16UT Mini-tower Workstation - 1 x Intel Xeon E3-1245V3 3.40 GHz. Hello, you are welcome. StackOverflow. some_email@hotmail.com"
        val sentences = message.split("(?<=[.!?])\\s* ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (s in sentences) {
            println(s)
        }
    }


    private fun decorateTextBasedOnTimeDistance(context: Context, text: String): Spannable {
        val decoratedSpan = SpannableString(text)

        val textColor: Int
        val typefaceStyle: Int



        textColor = R.color.secondaryTextColor
        typefaceStyle = Typeface.NORMAL


        decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, textColor)),
                0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        decoratedSpan.setSpan(StyleSpan(typefaceStyle),
                0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

        return decoratedSpan
    }

    fun sampleClickable() : SpannableStringBuilder{
        val ssb = SimpleSpanBuilder()
        var floatt : Float = 1.5F
        ssb.append("Hello")
        ssb.append(" ")
        var anuu : RelativeSizeSpan = RelativeSizeSpan(floatt)

        ssb.append("stackOverflow", ForegroundColorSpan(Color.RED), anuu)
        return ssb.build()
    }

}

