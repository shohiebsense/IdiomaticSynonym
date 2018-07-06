package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.LanguageSettingActivity
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.ChosenLanguage


/**
 * Created by Shohiebsense on 13/06/2018
 */

class LanguageSettingItem(var listener: SettingItemClickListener, val language : ChosenLanguage) : AbstractItem<LanguageSettingItem, LanguageSettingViewHolder>() {

    override fun getType(): Int = R.id.fastadapter_sample_item_id

    override fun getViewHolder(v: View): LanguageSettingViewHolder = LanguageSettingViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_language

    interface SettingItemClickListener{
        fun onItemClick(language : String)
    }

}