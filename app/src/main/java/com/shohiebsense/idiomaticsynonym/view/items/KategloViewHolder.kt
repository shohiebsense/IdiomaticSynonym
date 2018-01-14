package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import kotlinx.android.synthetic.main.item_idioms.view.*

/**
 * Created by Shohiebsense on 14/12/2017.
 */
class KategloViewHolder(itemView : View): FastAdapter.ViewHolder<KategloItem>(itemView) {


    override fun bindView(item: KategloItem?, payloads: MutableList<Any>?) {


    }

    override fun unbindView(item: KategloItem?) {
        itemView.itemTextView.text = item!!.text
        itemView.setOnClickListener {
            item.itemClickListener.onKategloItemClick(item!!.text, item.chosenSynonymWord.index)
        }
    }

    interface KategloItemListener {
        fun onKategloItemClick(view: CharSequence, index: Int)
    }
}