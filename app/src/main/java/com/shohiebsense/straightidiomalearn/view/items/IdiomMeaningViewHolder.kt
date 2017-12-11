package com.shohiebsense.straightidiomalearn.view.items

import android.view.View
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.utils.AppUtil

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomMeaningViewHolder : FastAdapter.ViewHolder<IdiomMeaningItem> {

    lateinit var view : View
    lateinit var idiomMeaningTextView : TextView


    constructor(itemView: View?) : super(itemView){
        this.view = itemView!!
        idiomMeaningTextView = view.findViewById(R.id.idiomTextView)
    }


    override fun unbindView(item: IdiomMeaningItem?) {
        idiomMeaningTextView.text = ""
    }

    override fun bindView(item: IdiomMeaningItem?, payloads: MutableList<Any>?) {
        var context = itemView.context
        idiomMeaningTextView.text = item!!.idiomMeaning.text

        view.setOnClickListener {
            AppUtil.makeDebugLog("clickedddd this item ")
            item.idiomItemClickListener.onIdiomItemClick(idiomMeaningTextView)

        }
    }


    interface IdiomItemClickListener {
        fun onIdiomItemClick(view : View)
    }






}
