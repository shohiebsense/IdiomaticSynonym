package com.shohiebsense.idiomaticsynonym.view.items

import android.content.Intent
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.square1.richtextlib.spans.ClickableSpan
import io.square1.richtextlib.ui.RichContentViewDisplay
import kotlinx.android.synthetic.main.item_bookmark.view.*

/**
 * Created by Shohiebsense on 16/01/2018.
 */
class BookmarkViewHolder(val v : View) : FastAdapter.ViewHolder<BookmarkItem>(v), View.OnClickListener {


    override fun bindView(item: BookmarkItem, payloads: MutableList<Any>?) {
        itemView.nameTextView.text = item.bookmark.fileName
        itemView.textsTextView.setText(item.bookmark.english)
        itemView.textsTextView.setOnSpanClickedObserver(object : RichContentViewDisplay.OnSpanClickedObserver{
            override fun onSpanClicked(span: ClickableSpan?): Boolean {
                AppUtil.makeDebugLog("CLICKEDDD CLICKEDD CLICKEDD")
                return true
            }

        })
        itemView.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        val intent = Intent(Intent(v.context, TranslatedDisplayActivity::class.java))
        v.context.startActivity(intent)
    }


    override fun unbindView(item: BookmarkItem?) {
    }


}