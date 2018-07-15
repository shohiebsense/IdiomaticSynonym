package com.shohiebsense.idiomaticsynonym.view.items

import android.app.Activity
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.ReplacedSentence

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class SentenceItem : AbstractItem<SentenceItem, SentenceViewHolder>() {
    lateinit var sentence : ReplacedSentence
    lateinit var activity : Activity
    lateinit var sentenceItemClickListener : SentenceViewHolder.SentenceItemClickListener

    fun withSentence(sentence: ReplacedSentence, sentenceItemClickListener: SentenceViewHolder.SentenceItemClickListener) : SentenceItem {
        this.sentence = sentence
        this.sentenceItemClickListener = sentenceItemClickListener
        return this
    }

    override fun getViewHolder(v: View): SentenceViewHolder = SentenceViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_sentence

    override fun getType(): Int = R.id.fastadapter_sample_item_id



}