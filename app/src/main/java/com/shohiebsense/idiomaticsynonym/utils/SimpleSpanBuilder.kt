package com.shohiebsense.idiomaticsynonym.utils

import android.text.ParcelableSpan
import android.text.Spannable
import android.text.SpannableStringBuilder


/**
 * Created by Shohiebsense on 10/10/2017.
 */

class SimpleSpanBuilder {

    private val spanSections: MutableList<SpanSection>
    private val stringBuilder: StringBuilder

    private inner class SpanSection (private val text: String, private val startIndex: Int, vararg spans: ParcelableSpan) {
        private val spans: Array<out ParcelableSpan> = spans

        fun apply(spanStringBuilder: SpannableStringBuilder?) {
            if (spanStringBuilder == null) return
            for (span in spans) {
                spanStringBuilder.setSpan(span, startIndex, startIndex + text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
    }

    init {
        stringBuilder = StringBuilder()
        spanSections = ArrayList()
    }

    fun append(text: String, vararg spans: ParcelableSpan): SimpleSpanBuilder {
        if (spans != null && spans.size > 0) {
            spanSections.add(SpanSection(text, stringBuilder.length, *spans))
        }
        stringBuilder.append(text)
        return this
    }


    fun build(): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(stringBuilder.toString())
        for (section in spanSections) {
            section.apply(ssb)
        }
        return ssb
    }

    override fun toString(): String {
        return stringBuilder.toString()
    }
}