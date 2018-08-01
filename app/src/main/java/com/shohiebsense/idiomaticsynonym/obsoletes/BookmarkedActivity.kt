package com.shohiebsense.idiomaticsynonym.obsoletes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.items.BookmarkItem
import kotlinx.android.synthetic.main.activity_bookmarked.*

class BookmarkedActivity : AppCompatActivity(), BookmarkDataEmitter.BookmarksCallback {
    override fun onError() {

    }


    lateinit var bookmarkFastAdapter : FastAdapter<BookmarkItem>
    lateinit var bookmarkItemAdapter : ItemAdapter<BookmarkItem>
    lateinit var bookmarks : ArrayList<BookmarkedEnglish>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmarked)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        bookmarkItemAdapter= ItemAdapter.items()
        bookmarkFastAdapter = FastAdapter.with(bookmarkItemAdapter)
        bookmarkRecyclerView.layoutManager = LinearLayoutManager(this)
        bookmarkRecyclerView.adapter = bookmarkFastAdapter
        //getIndexedSentence()
    }

    /*fun getIndexedSentence(){
        val bookmarkDataEmitter = BookmarkDataEmitter(this)
        bookmarkDataEmitter.insertBookmarkEnglish("fileName","whoooolleee text","haloo ini indonesia")
        bookmarkDataEmitter.queryService.countBookmarkEnglishTable()
        bookmarkDataEmitter.getEnglisbBookmarks(this)
    }*/

    override fun onFetched(bookmarks: ArrayList<BookmarkedEnglish>) {
        emptyTextView.visibility = if(bookmarks.isEmpty()) View.VISIBLE else View.GONE
        this.bookmarks = bookmarks
        AppUtil.makeDebugLog("jumlahnyaaa "+bookmarks.size)
        var items = mutableListOf<BookmarkItem>()

        bookmarks.forEach {
            it->
            var idiomMeaningItem = BookmarkItem().withSentence(it)
            items.add(idiomMeaningItem)
        }
        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    bookmarkItemAdapter.clear()
                    bookmarkItemAdapter.add(items)
                    bookmarkRecyclerView.adapter = bookmarkFastAdapter
                    bookmarkFastAdapter.notifyAdapterDataSetChanged()
                }
            }
        }.start()

    }

}
