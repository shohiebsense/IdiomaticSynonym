package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import kotlinx.android.synthetic.main.item_statistic.view.*

/**
 * Created by Shohiebsense on 03/02/2018.
 */
class StatisticViewHolder(v : View) : FastAdapter.ViewHolder<StatisticItem>(v) {
    override fun unbindView(item: StatisticItem?) {

    }

    override fun bindView(item: StatisticItem, payloads: MutableList<Any>?) {
        itemView.statisticValueTextView.text = item.valueText
        itemView.statisticSubTitleTextView.text = item.titleText
    }
}