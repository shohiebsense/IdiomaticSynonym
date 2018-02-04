package com.shohiebsense.idiomaticsynonym.view.items

import android.view.View
import com.mikepenz.fastadapter.items.AbstractItem
import com.shohiebsense.idiomaticsynonym.BookmarkedActivity
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.utils.AppUtil

/**
 * Created by Shohiebsense on 16/01/2018.
 */
class BookmarkItem : AbstractItem<BookmarkItem, BookmarkViewHolder>() {

    lateinit var bookmark : BookmarkedEnglish

    fun withSentence(bookmark: BookmarkedEnglish) : BookmarkItem{
        this.bookmark = bookmark
        return this
    }
    override fun getViewHolder(v: View): BookmarkViewHolder = BookmarkViewHolder(v)

    override fun getLayoutRes(): Int = R.layout.item_bookmark


    override fun getType(): Int  = 0


}