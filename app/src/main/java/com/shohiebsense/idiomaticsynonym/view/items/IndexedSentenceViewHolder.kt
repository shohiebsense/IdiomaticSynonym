package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import kotlinx.android.synthetic.main.obsolete_item_indexed_sentence.view.*

/**
 * Created by Shohiebsense on 03/01/2018.
 */
class IndexedSentenceViewHolder(itemView: View) : FastAdapter.ViewHolder<IndexedSentenceItem>(itemView) {



    override fun unbindView(item: IndexedSentenceItem?) {
    }

    override fun bindView(item: IndexedSentenceItem, payloads: MutableList<Any>?) {
        itemView.sentenceNumber.text = item.indexedSentence.index.toString()

        itemView.setOnClickListener {
            item.indexedSentenceClickListener.onItemClick(item.indexedSentence.index!!)
        }
    }


    interface IndexedSentenceClickListener {
        fun onItemClick(index: Int)
    }
}