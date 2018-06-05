package com.shohiebsense.idiomaticsynonym

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.services.UploadService
import com.shohiebsense.idiomaticsynonym.services.dbs.BookmarkQueryService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.TranslatedDisplayPagerAdapter
import kotlinx.android.synthetic.main.activity_translated_display.*
import org.jetbrains.anko.contentView
import android.app.Activity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.model.event.*
import com.shohiebsense.idiomaticsynonym.services.WordClickableService
import com.shohiebsense.idiomaticsynonym.services.kateglo.KategloService
import com.shohiebsense.idiomaticsynonym.view.callbacks.WordClickableCallback
import kotlinx.android.synthetic.main.fragment_indexedsentencelist.*
import org.greenrobot.eventbus.EventBus


class TranslatedDisplayActivity : AppCompatActivity(), BookmarkQueryService.CompletedTransactionListener, WordClickableCallback, KategloService.KategloListener {

    override fun onCompleted(links: ArrayList<Link>) {
        AppUtil.makeErrorLog("hii this is from english fragment ")
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentLinksEvent(links))
        }
        else{
            EventBus.getDefault().post(IdiomsSummarryLinksEvent(links))
        }
    }

    override fun onClickedIdiomText(idiom: String) {
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentIdiomEvent(idiom))
        }
        else{
            EventBus.getDefault().post(IdiomsSummarrydiomEvent(idiom))
        }
    }

    override fun onShowingOnlineTranslation(meanings: MutableList<String>) {
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentMeaningsEvent(meanings))
        }
        else{
            EventBus.getDefault().post(IdiomsSummaryMeaningsEvent(meanings))
        }
    }


    lateinit var fileName : String
    lateinit var indexedSentenceList : ArrayList<IndexedSentence>
    lateinit var translatedTexts : ArrayList<String>
    lateinit var indices : ArrayList<Int>
    lateinit var texts : CharSequence
    var lastId = 0
    var isFromBookmarkItem = false
    lateinit var uploadService : UploadService
    var saveMenuItem : MenuItem? = null
    var uploadMenuItem : MenuItem? = null
    var docViewMenuItem : MenuItem? = null
    var toggleEachLineItem : MenuItem? = null
    var toggleIdiomCardItem : MenuItem? = null
    var isWrapped = true
    var isSlideShow = true
    var isToggleVisible = false
    var isToggleCardVisible = false
    var isFromEnglishFragment = true
    lateinit var wordClickableService: WordClickableService
    lateinit var kategloService : KategloService
    var currentSelectedWord = ""


    companion object {
        val INTENT_LAST_ID = "lastid"
        val INTENT_IS_FROM_BOOKMARKITEM = "isfrombookmarkitem"
        val UPDATE_RESULT = 1
        @JvmStatic val INTENT_FILENAME = "FILENAME"
        @JvmStatic var INTENT_MESSAGE = "INTENT_MESSAGE"
        @JvmStatic var INTENT_FETCHED_TEXT = "FETCHED_TEXT_MESSAGE"
        @JvmStatic var INTENT_TRANSLATED_TEXT = "TRANSLATED_TEXT_MESSAGE"
        @JvmStatic var INTENT_IDIOM_LIST = "IDIOM_LIST_MESSAGE"
        @JvmStatic val INTENT_INDICES = "IDIOM_INDICES"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordClickableService = WordClickableService(this,this)
        kategloService = KategloService()
        setContentView(R.layout.activity_translated_display)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        if(intent != null){
            AppUtil.makeDebugLog("Translated display isnt null")
            fileName = intent.getStringExtra(INTENT_FILENAME)
            lastId = intent.getIntExtra(INTENT_LAST_ID,0)
            isFromBookmarkItem = intent.getBooleanExtra(INTENT_IS_FROM_BOOKMARKITEM,false)
            invalidateOptionsMenu()
            toolbar.title = AppUtil.getOnlyFileName(fileName)
            //bookmarkDataEmitter.getAllIndexedSentenceBasedOnLastId(lastId,this)
        }
        val adapter = TranslatedDisplayPagerAdapter(this, supportFragmentManager, lastId)
        translatedDisplayViewPager.offscreenPageLimit = 3
        translatedDisplayViewPager.adapter = adapter
        translatedDisplayTabLayout.setupWithViewPager(translatedDisplayViewPager)

    }

    fun getSynonym(word : String){
        currentSelectedWord = word
        kategloService.getSynonymStrings(word,this)
    }

    override fun onGetSyonyms(syonyms: MutableList<String>) {
        syonyms.add(0,currentSelectedWord)
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentSynonymEvent(syonyms))
        }
        else{
            EventBus.getDefault().post(IdiomsSummarySynonymEvent(syonyms))
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(this,MainActivity::class.java))
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        translatedDisplayViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                AppUtil.makeErrorLog("the position iss "+position)
                isToggleVisible = position == 1
                isToggleCardVisible = position == 2
                invalidateOptionsMenu()
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_translated_display, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        docViewMenuItem = menu?.findItem(R.id.docsViewOption)
        saveMenuItem = menu?.findItem(R.id.saveOption)
        uploadMenuItem = menu?.findItem(R.id.uploadOption)
        toggleEachLineItem = menu?.findItem(R.id.toggleEachLineOption)
        toggleIdiomCardItem = menu?.findItem(R.id.toggleIdiomCardOption)
        if(isFromBookmarkItem){
            saveMenuItem?.setVisible(false)
            uploadMenuItem?.setVisible(false)
            docViewMenuItem?.setVisible(true)
        }
        else{
            uploadMenuItem?.setVisible(true)
            docViewMenuItem?.setVisible(false)
            saveMenuItem?.setVisible(true)
        }
        toggleEachLineItem?.isVisible = isToggleVisible
        toggleIdiomCardItem?.isVisible = isToggleCardVisible
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.saveOption -> {
                val bookmarkDataEmitter = BookmarkDataEmitter(this)

            }
            R.id.uploadOption -> {
                uploadService = UploadService(this)
                uploadService.upload(lastId,fileName)
            }
            R.id.docsViewOption -> {
                val intent = Intent(this,DriveOpenFileActivity::class.java)
                intent.putExtra(DriveOpenFileActivity.INTENT_ID,lastId)
                startActivity(intent)
            }
            R.id.updateIndonesianOption ->{
                val intent = Intent(this,UpdateIndonesianActivity::class.java)
                intent.putExtra(UpdateIndonesianActivity.INTENT_ID,lastId)
                startActivityForResult(intent, UPDATE_RESULT)
            }
            R.id.toggleEachLineOption ->{
                isWrapped = !isWrapped
                if(isWrapped){
                    toggleEachLineItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_sort_white_24))
                }
                else{
                    toggleEachLineItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_wrap_text_white_24))
                }
                EventBus.getDefault().post(ViewEvent(isWrapped))
            }
            R.id.toggleIdiomCardOption ->{
                isSlideShow = !isSlideShow
                if(isSlideShow){
                    toggleIdiomCardItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_view_comfy_white_24))
                }
                else{
                    toggleIdiomCardItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_style_white_24))
                }
                EventBus.getDefault().post(IdiomCardViewEvent(isSlideShow))
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCompleted() {
        Snackbar.make(contentView!!, getString(R.string.text_finished_saving), Snackbar.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATE_RESULT) {
            if (resultCode == Activity.RESULT_OK) {
                val adapter = TranslatedDisplayPagerAdapter(this, supportFragmentManager, lastId)
                translatedDisplayViewPager.offscreenPageLimit = 3
                translatedDisplayViewPager.adapter = adapter
                translatedDisplayTabLayout.setupWithViewPager(translatedDisplayViewPager)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
