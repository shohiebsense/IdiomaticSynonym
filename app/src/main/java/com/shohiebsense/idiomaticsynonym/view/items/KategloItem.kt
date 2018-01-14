package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.api.ChosenSynonymWord

/**
 * Created by Shohiebsense on 14/12/2017.
 */
class KategloItem : AbstractItem<KategloItem, KategloViewHolder>() {
    lateinit var text : String
    lateinit var chosenSynonymWord : ChosenSynonymWord
    lateinit var itemClickListener : KategloViewHolder.KategloItemListener

    fun withText(chosenSynonymWord: ChosenSynonymWord, itemClickListener : KategloViewHolder.KategloItemListener) : KategloItem {
        this.chosenSynonymWord = chosenSynonymWord
        this.text = chosenSynonymWord.text
        this.itemClickListener = itemClickListener
        return this
    }


    override fun getType(): Int = R.id.fastadapter_sample_item_id

    override fun getViewHolder(v: View): KategloViewHolder = KategloViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_idioms
}