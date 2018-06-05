package com.shohiebsense.idiomaticsynonym.view.items

import android.content.Intent
import android.text.Html
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
class BookmarkViewHolder(val v : View) : FastAdapter.ViewHolder<BookmarkItem>(v) {


    override fun bindView(item: BookmarkItem, payloads: MutableList<Any>?) {
        val name = AppUtil.getOnlyFileName(item.bookmark.fileName)
        itemView.nameTextView.text = name
        /*if(item.bookmark.english.length  >= 250){
            var summarytext : CharSequence = item.bookmark.english.subSequence(0,250)
            itemView.textsTextView.setText(StringBuffer(summarytext).append("..."))
        }*/
        itemView.textsTextView.setText(Html.fromHtml(item.bookmark.english.toString()).toString())
        val idioms = AppUtil.getListOfIdioms(item.bookmark.idioms)
        AppUtil.makeDebugLog("apaa sih "+item.bookmark.idioms)

        itemView.textsTextView.setOnSpanClickedObserver(object : RichContentViewDisplay.OnSpanClickedObserver{
            override fun onSpanClicked(span: ClickableSpan?): Boolean {

                AppUtil.makeDebugLog("CLICKEDDD CLICKEDD CLICKEDD  "+span!!.action + "   "+span.toString())
                return true
            }

        })
        itemView.setOnClickListener{
            val intent = Intent(Intent(v.context, TranslatedDisplayActivity::class.java))
            intent.putExtra(TranslatedDisplayActivity.INTENT_LAST_ID, item.bookmark.id)
            intent.putExtra(TranslatedDisplayActivity.INTENT_FILENAME, item.bookmark.fileName)
            intent.putExtra(TranslatedDisplayActivity.INTENT_IS_FROM_BOOKMARKITEM, true)
            v.context.startActivity(intent)
        }
    }

    override fun unbindView(item: BookmarkItem?) {
    }


}