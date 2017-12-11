package com.shohiebsense.straightidiomalearn.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.straightidiomalearn.R

/**
 * Created by Shohiebsense on 23/11/2017.
 */


class FactCardItem : AbstractItem<FactCardItem, FactCardViewHolder>() {
    var imageId  = Int




    override fun getViewHolder(v: View?): FactCardViewHolder =  FactCardViewHolder(v)
    override fun getLayoutRes(): Int = R.layout.item_idioms
    override fun getType(): Int = R.id.fastadapter_factcard_item_id

}