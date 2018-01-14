package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence

/**
 * Created by Shohiebsense on 03/01/2018.
 */
class IndexedSentenceItem() : AbstractItem<IndexedSentenceItem, IndexedSentenceViewHolder>()  {
    override fun getType(): Int = R.id.fastadapter_sample_item_id

    override fun getViewHolder(v: View): IndexedSentenceViewHolder = IndexedSentenceViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_indexed_sentence

    lateinit var indexedSentence : IndexedSentence
    lateinit var indexedSentenceClickListener: IndexedSentenceViewHolder.IndexedSentenceClickListener



    fun withSentence(indexedSentence: IndexedSentence, indexedSentenceClickListener: IndexedSentenceViewHolder.IndexedSentenceClickListener) : IndexedSentenceItem{
        this.indexedSentence = indexedSentence
        this.indexedSentenceClickListener = indexedSentenceClickListener
        return this
    }

}