package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.ReplacedSentence
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import kotlinx.android.synthetic.main.item_idioms.view.*
import kotlinx.android.synthetic.main.item_sentence.view.*

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class SentenceViewHolder(val view : View) : FastAdapter.ViewHolder<SentenceItem>(view) {



    override fun unbindView(p0: SentenceItem?) {
        view.text_sentence.text = ""

    }

    override fun bindView(p0: SentenceItem?, p1: MutableList<Any>?) {
        AppUtil.makeErrorLog("enggak ke sini kanh??")
        view.text_sentence.text = p0!!.sentence.sentence
        view.text_sentence.setOnClickListener {
            p0.sentenceItemClickListener.onSentenceClick(p0.sentence)
        }
    }



    interface SentenceItemClickListener{
        fun onSentenceClick(sentence : ReplacedSentence)
    }

}
