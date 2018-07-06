package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R


/**
 * Created by Shohiebsense on 13/06/2018
 */

class SettingItem : AbstractItem<SettingItem, SettingViewHolder>() {
    lateinit var titleText: String
    lateinit var valueText: String

    fun withSetting(titleText : String, valueText : String) : SettingItem{
        this.titleText = titleText
        this.valueText = valueText
        return this
    }
    override fun getType(): Int = R.id.fastadapter_sample_item_id

    override fun getViewHolder(v: View): SettingViewHolder = SettingViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_settings

}