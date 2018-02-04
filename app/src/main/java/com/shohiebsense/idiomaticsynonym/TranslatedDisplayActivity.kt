package com.shohiebsense.idiomaticsynonym

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.services.dbs.BookmarkQueryService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.TranslatedDisplayPagerAdapter
import kotlinx.android.synthetic.main.activity_translated_display.*
import org.jetbrains.anko.contentView

class TranslatedDisplayActivity : FragmentActivity(), BookmarkQueryService.CompletedTransactionListener, BookmarkDataEmitter.BookmarkAndIndexedSentenceCallback {


    lateinit var fileName : String
    lateinit var indexedSentenceList : ArrayList<IndexedSentence>
    lateinit var translatedTexts : ArrayList<String>
    lateinit var indices : ArrayList<Int>
    lateinit var texts : CharSequence
    var lastId = 0

    companion object {
        val INTENT_LAST_ID = "lastid"
        @JvmStatic val INTENT_FILENAME = "FILENAME"
        @JvmStatic var INTENT_MESSAGE = "INTENT_MESSAGE"
        @JvmStatic var INTENT_FETCHED_TEXT = "FETCHED_TEXT_MESSAGE"
        @JvmStatic var INTENT_TRANSLATED_TEXT = "TRANSLATED_TEXT_MESSAGE"
        @JvmStatic var INTENT_IDIOM_LIST = "IDIOM_LIST_MESSAGE"
        @JvmStatic val INTENT_INDICES = "IDIOM_INDICES"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translated_display)
        val bookmarkDataEmitter = BookmarkDataEmitter(this)

        if(intent != null){
            AppUtil.makeDebugLog("Translated display isnt null")
            fileName = intent.getStringExtra(INTENT_FILENAME)
            lastId = intent.getIntExtra(INTENT_LAST_ID,0)
            //bookmarkDataEmitter.getAllIndexedSentenceBasedOnLastId(lastId,this)
        }
        val adapter = TranslatedDisplayPagerAdapter(this, supportFragmentManager, lastId)
        translatedDisplayViewPager.adapter = adapter
        translatedDisplayTabLayout.setupWithViewPager(translatedDisplayViewPager)
    }

    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_translated_display, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.saveOption -> {
                val bookmarkDataEmitter = BookmarkDataEmitter(this)
                bookmarkDataEmitter.insertBookmarkEnglish(fileName, texts,"")
                bookmarkDataEmitter.insertIndexedSentences(indexedSentenceList,this)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCompleted() {
        Snackbar.make(contentView!!, getString(R.string.text_finished_saving), Snackbar.LENGTH_LONG).show()
    }

    override fun onFetched(bookmark: BookmarkedEnglish, indexedSentences: ArrayList<IndexedSentence>) {
        AppUtil.makeDebugLog("onFetched... ")
        AppUtil.makeDebugLog("bookmarksize "+bookmark.fileName)
        AppUtil.makeDebugLog("index size "+indexedSentences.size)
        AppUtil.makeDebugLog("bookmak indonesian "+bookmark.indonesian)

    }

}
