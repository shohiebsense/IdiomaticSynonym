package com.shohiebsense.idiomaticsynonym.view.items

import android.app.Activity
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomItem : AbstractItem<IdiomItem, IdiomViewHolder>() {
    lateinit var idiom : String
    lateinit var activity : Activity
    lateinit var idiomItemClickListener : IdiomMeaningViewHolder.IdiomItemClickListener

    fun withIdiomMeaning(meaning : String, idiomItemClickListener: IdiomMeaningViewHolder.IdiomItemClickListener) : IdiomItem {
        this.idiom = meaning
        this.idiomItemClickListener = idiomItemClickListener
        return this
    }

    override fun getViewHolder(v: View?): IdiomViewHolder = IdiomViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_idioms

    override fun getType(): Int = R.id.fastadapter_sample_item_id



}