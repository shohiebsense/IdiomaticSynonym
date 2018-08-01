package com.shohiebsense.idiomaticsynonym.view.items

import android.content.Intent
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import kotlinx.android.synthetic.main.item_language.view.*


/**
 * Created by Shohiebsense on 13/06/2018
 */

class SettingViewHolder (itemView : View): FastAdapter.ViewHolder<SettingItem>(itemView) {
    override fun bindView(item: SettingItem, payloads: MutableList<Any>?) {
        itemView.text_title.setText(item.titleText)
        itemView.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context,Class.forName(item.valueText)))
        }
    }

    override fun unbindView(item: SettingItem?) {
    }
}