package com.shohiebsense.idiomaticsynonym.view.items

import android.app.Activity
import android.support.annotation.StringRes
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomMeaningItem : AbstractItem<IdiomMeaningItem, IdiomMeaningViewHolder>() {
    lateinit var idiomMeaning : String
    lateinit var activity : Activity
    lateinit var idiomItemClickListener : IdiomMeaningViewHolder.IdiomItemClickListener
    var index : Int = 0

    fun withIdiomMeaning(meaning : String, idiomItemClickListener: IdiomMeaningViewHolder.IdiomItemClickListener, index : Int) : IdiomMeaningItem {
        this.idiomMeaning = meaning
        this.idiomItemClickListener = idiomItemClickListener
        AppUtil.makeDebugLog("with indexx "+index)
        this.index = index
        return this
    }

    override fun getViewHolder(v: View?): IdiomMeaningViewHolder = IdiomMeaningViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_idioms

    override fun getType(): Int = R.id.fastadapter_sample_item_id



}