package com.shohiebsense.idiomaticsynonym.view.items

import android.support.v4.content.ContextCompat
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.LocaleManager
import kotlinx.android.synthetic.main.item_language.view.*


/**
 * Created by Shohiebsense on 13/06/2018
 */

class LanguageSettingViewHolder(itemView : View): FastAdapter.ViewHolder<LanguageSettingItem>(itemView) {
    override fun bindView(item: LanguageSettingItem, payloads: MutableList<Any>?) {
        itemView.text_title.setText(item.language.languageName)
        val currentLanguage = LocaleManager.getLanguage(itemView.context)
        if(currentLanguage.equals(item.language.languageCode,true)) {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.material_red_a100))
        }
        itemView.setOnClickListener {
            item!!.listener.onItemClick(item.language.languageName)
        }
    }

    override fun unbindView(item: LanguageSettingItem?) {
    }
}