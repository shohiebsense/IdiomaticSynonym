package com.shohiebsense.idiomaticsynonym.view.items

import android.content.Intent
import android.text.Html
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.shohiebsense.idiomaticsynonym.view.activity.detail.DetailActivity
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import kotlinx.android.synthetic.main.item_bookmark.view.*

/**
 * Created by Shohiebsense on 16/01/2018.
 */
class BookmarkViewHolder(val v : View) : FastAdapter.ViewHolder<BookmarkItem>(v) {


    override fun bindView(item: BookmarkItem, payloads: MutableList<Any>?) {
        val name = AppUtil.getOnlyFileName(item.bookmark.fileName)
        itemView.nameTextView.text = name
        itemView.textsTextView.setText (Html.fromHtml(item.bookmark.english.toString()).toString())
        val idioms = AppUtil.getListOfIdioms(item.bookmark.idioms)
        AppUtil.makeDebugLog("apaa sih "+item.bookmark.idioms)
        itemView.card_phone_balance_transfer.setOnClickListener{
            navigateToTranslationDetail(item)
        }
        itemView.textsTextView.setOnClickListener{
           navigateToTranslationDetail(item)
        }
    }

    fun navigateToTranslationDetail(item : BookmarkItem){
        val intent = Intent(Intent(v.context, DetailActivity::class.java))
        intent.putExtra(DetailActivity.INTENT_LAST_ID, item.bookmark.id)
        intent.putExtra(DetailActivity.INTENT_FILENAME, item.bookmark.fileName)
        intent.putExtra(DetailActivity.INTENT_IS_TRANSLATION_EMPTY, item.bookmark.indonesian?.isBlank())
        intent.putExtra(DetailActivity.INTENT_IS_FROM_BOOKMARK_ITEM,true)
        v.context.startActivity(intent)
    }

    override fun unbindView(item: BookmarkItem?) {

    }


}