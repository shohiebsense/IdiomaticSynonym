package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R

/**
 * Created by Shohiebsense on 03/02/2018.
 */
class StatisticItem : AbstractItem<StatisticItem, StatisticViewHolder>() {
    lateinit var titleText : String
    lateinit var valueText : String

    fun withStatistic(titleText : String, valueText : String) : StatisticItem{
        this.titleText = titleText
        this.valueText = valueText
        return this
    }

    override fun getType(): Int = R.id.fastadapter_sample_item_id

    override fun getViewHolder(v: View): StatisticViewHolder = StatisticViewHolder(v)

    override fun getLayoutRes(): Int =  R.layout.item_statistic


}