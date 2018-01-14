package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import android.widget.ImageView
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.R

/**
 * Created by Shohiebsense on 23/11/2017.
 */

class FactCardViewHolder : FastAdapter.ViewHolder<FactCardItem> {

    lateinit var rootView : View
    lateinit var factCardView: View
    lateinit var factCardImageView: ImageView


    constructor(itemView: View?) : super(itemView){
        rootView = itemView!!
        factCardImageView = factCardView.findViewById(R.id.itemTextView)
    }

    override fun bindView(item: FactCardItem?, payloads: MutableList<Any>?) {


    }

    override fun unbindView(item: FactCardItem?) {
    }


}