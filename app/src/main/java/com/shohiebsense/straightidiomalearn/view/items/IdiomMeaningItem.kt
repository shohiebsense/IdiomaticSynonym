package com.shohiebsense.straightidiomalearn.view.items

import android.app.Activity
import android.support.annotation.StringRes
import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.materialize.holder.StringHolder
import com.shohiebsense.straightidiomalearn.R

/**
 * Created by Shohiebsense on 20/11/2017.
 */

class IdiomMeaningItem() : AbstractItem<IdiomMeaningItem, IdiomMeaningViewHolder>() {
    lateinit var idiomMeaning : StringHolder
    lateinit var activity : Activity
    lateinit var idiomItemClickListener : IdiomMeaningViewHolder.IdiomItemClickListener

    fun withIdiomMeaning(@StringRes nameRes : Int) : IdiomMeaningItem {
        this.idiomMeaning = StringHolder(nameRes)
        return this
    }

    fun withIdiomMeaning(meaning : String, idiomItemClickListener: IdiomMeaningViewHolder.IdiomItemClickListener) : IdiomMeaningItem {
        this.idiomMeaning = StringHolder(meaning)
        this.idiomItemClickListener = idiomItemClickListener
        return this
    }

    override fun getViewHolder(v: View?): IdiomMeaningViewHolder = IdiomMeaningViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_idioms

    override fun getType(): Int = R.id.fastadapter_sample_item_id



}