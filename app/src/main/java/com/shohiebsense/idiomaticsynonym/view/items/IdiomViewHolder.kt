package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomViewHolder : FastAdapter.ViewHolder<IdiomMeaningItem> {

    lateinit var view : View
    lateinit var idiomTextView : TextView


    constructor(itemView: View?) : super(itemView){
        this.view = itemView!!
        idiomTextView = view.findViewById(R.id.itemTextView)
    }


    override fun unbindView(item: IdiomMeaningItem?) {
        idiomTextView.text = ""
    }

    override fun bindView(item: IdiomMeaningItem?, payloads: MutableList<Any>?) {
        var context = itemView.context
        idiomTextView.text = item!!.idiomMeaning

        view.setOnClickListener {
            AppUtil.makeDebugLog("clickedddd this item ")
            item.idiomItemClickListener.onIdiomItemClick(item.idiomMeaning)
        }
    }








}
