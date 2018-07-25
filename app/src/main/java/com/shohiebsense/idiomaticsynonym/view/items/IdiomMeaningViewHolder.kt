package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import kotlinx.android.synthetic.main.item_idioms.view.*

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomMeaningViewHolder : FastAdapter.ViewHolder<IdiomMeaningItem> {

    lateinit var view : View
    lateinit var idiomMeaningTextView : TextView


    constructor(itemView: View?) : super(itemView){
        this.view = itemView!!
        idiomMeaningTextView = view.findViewById(R.id.itemTextView)
    }


    override fun unbindView(item: IdiomMeaningItem?) {
        idiomMeaningTextView.text = ""
    }

    override fun bindView(item: IdiomMeaningItem?, payloads: MutableList<Any>?) {
        var context = itemView.context
        idiomMeaningTextView.text = item!!.idiomMeaning

        view.itemTextView.setOnClickListener {
            item.idiomItemClickListener.onIdiomItemClick(item.idiomMeaning)
        }
    }


    interface IdiomItemClickListener {
        fun onIdiomItemClick(word: String)
    }






}
