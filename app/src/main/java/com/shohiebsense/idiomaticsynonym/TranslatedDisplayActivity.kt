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
import org.jetbrains.anko.contentView
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.widget.ToggleButton
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.event.*
import com.shohiebsense.idiomaticsynonym.services.WordClickableService
import com.shohiebsense.idiomaticsynonym.services.kateglo.KategloService
import com.shohiebsense.idiomaticsynonym.view.callbacks.WordClickableCallback
import de.mateware.snacky.Snacky
import kotlinx.android.synthetic.main.activity_translated_display.*
import kotlinx.android.synthetic.main.view_idiom_item_toggle.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class TranslatedDisplayActivity : AppCompatActivity(), BookmarkQueryService.CompletedTransactionListener, WordClickableCallback, KategloService.KategloListener, BookmarkDataEmitter.SingleBookmarkCallback, BookmarkDataEmitter.UpdateBookmarkCallback {

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

    override fun onShowingTranslation(meanings: MutableList<String>) {
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentMeaningsEvent(meanings))
        }
        else{
            EventBus.getDefault().post(IdiomsSummaryMeaningsEvent(meanings))
        }
    }

    override fun onShowingIdiomOnlineTranslation(translation: String) {
        EventBus.getDefault().post(IdiomTranslationEvent(translation))
    }

    lateinit var fileName : String
    lateinit var indexedSentenceList : ArrayList<IndexedSentence>
    lateinit var translatedTexts : ArrayList<String>
    lateinit var indices : ArrayList<Int>
    lateinit var texts : CharSequence
    var lastId = 0
    var isTranslationEmpty = false
    lateinit var uploadService : UploadService
    var generateFileOption : MenuItem? = null
    var toggleEachLineItem : MenuItem? = null
    var toggleIdiomCardItem : MenuItem? = null
    var toggleTranslateItem : MenuItem? = null
    var isWrapped = true
    var isSlideShow = true
    var isToggleVisible = false
    var isToggleCardVisible = false
    var isFromEnglishFragment = true
    var isFromBookmarkItem = false
    var isFetched = false
    var isIdiomSynonymMode = true
    var toggleTranslateButton : ToggleButton? = null
    lateinit var wordClickableService: WordClickableService
    lateinit var kategloService : KategloService
    var currentSelectedWord = ""
    lateinit var bookmarkDataEmitter : BookmarkDataEmitter



    companion object {
        val INTENT_LAST_ID = "lastid"
        val INTENT_IS_TRANSLATION_EMPTY = "isuploadidempty"
        val INTENT_IS_FROM_BOOKMARK_ITEM ="isfrombookmarkitem"
        val UPDATE_RESULT = 1
        val DOCS_VIEW_RESULT = 2
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
            isTranslationEmpty = intent.getBooleanExtra(INTENT_IS_TRANSLATION_EMPTY,false)
            isFromBookmarkItem = intent.getBooleanExtra(INTENT_IS_FROM_BOOKMARK_ITEM, false)
            invalidateOptionsMenu()
            toolbar.title = AppUtil.getOnlyFileName(fileName)
            bookmarkDataEmitter = BookmarkDataEmitter(this)
        }
        val adapter = TranslatedDisplayPagerAdapter(this, supportFragmentManager, lastId)
        translatedDisplayViewPager.offscreenPageLimit = 3
        translatedDisplayViewPager.adapter = adapter
        translatedDisplayTabLayout.setupWithViewPager(translatedDisplayViewPager)


        if(!AppUtil.getIdiomGuidance(this)){
            Snacky.builder().setActivity(this).success().setText(getString(R.string.idiom_guidance)).setDuration(Snacky.LENGTH_LONG).show()
            AppUtil.setIdiomGuidance(this,true)
        }
        refresh()
    }

    fun getSynonym(word : String){
        currentSelectedWord = word
        kategloService.getSynonymStrings(word,this)
    }

    override fun onGetSyonyms(syonyms: MutableList<String>) {
        syonyms.add(0,currentSelectedWord)
        AppUtil.makeErrorLog("get synonymss "+syonyms.size)
        if(isFromEnglishFragment){
            EventBus.getDefault().post(EnglishFragmentSynonymEvent(syonyms))
        }
        else{
            EventBus.getDefault().post(IdiomsSummarySynonymEvent(syonyms))
        }
    }

    var dialogClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
            }
        }
    }


    override fun onBackPressed() {
        if(!isTranslationEmpty && !isFromBookmarkItem){
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.confirmation_back_no_upload)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).show()
            return
        }
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
        translatedDisplayViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                isToggleVisible = position == 1
                isToggleCardVisible = position == 2
                if(toggleTranslateButton != null){
                    toggleTranslateButton?.isChecked = !isIdiomSynonymMode
                }
                invalidateOptionsMenu()
            }

        })


    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFragmentReady(event : FragmentEvent){

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_translated_display, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        generateFileOption = menu?.findItem(R.id.generateFileOption)
        toggleEachLineItem = menu?.findItem(R.id.toggleEachLineOption)
        toggleIdiomCardItem = menu?.findItem(R.id.toggleIdiomCardOption)
        toggleTranslateItem = menu?.findItem(R.id.toggleTranslateOption)
        if(!isTranslationEmpty){
            generateFileOption?.setVisible(true)
        }
        else{
            generateFileOption?.setVisible(false)
        }
        toggleEachLineItem?.isVisible = isToggleVisible
        toggleIdiomCardItem?.isVisible = isToggleCardVisible
        toggleTranslateItem?.isVisible = !isToggleVisible && !isToggleCardVisible
        toggleTranslateButton = toggleTranslateItem?.actionView!!.toggle_button_translate
        toggleTranslateButton!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                toggleTranslateButton!!.setTextColor(ContextCompat.getColor(this,R.color.soft_white))
            }
            else{
                toggleTranslateButton!!.setTextColor(ContextCompat.getColor(this,R.color.moreYellowColor))
            }
            isIdiomSynonymMode = !isIdiomSynonymMode
            AppUtil.makeDebugLog("is in what mode, idiom mode? "+isIdiomSynonymMode)
        }
        if(toggleTranslateButton!!.isChecked){
            toggleTranslateButton!!.setTextColor(ContextCompat.getColor(this,R.color.soft_white))
        }
        else{
            toggleTranslateButton!!.setTextColor(ContextCompat.getColor(this,R.color.moreYellowColor))
        }
        return super.onPrepareOptionsMenu(menu)
    }

    lateinit var bookmark : BookmarkedEnglish
    override fun onFetched(bookmark: BookmarkedEnglish) {
        this.bookmark = bookmark
        EventBus.getDefault().post(BookmarkViewEvent())
        AppUtil.makeErrorLog("hoiii "+bookmark.id)
    }


    override fun onFailedFetched() {

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.generateFileOption -> {
                /*val bookmarkDataEmitter = BookmarkDataEmitter(this!!)
                bookmarkDataEmitter.getEnglishBookmark(lastId,this)*/
                val intent = Intent(this,CreateFileActivity::class.java)
                intent.putExtra(CreateFileActivity.INTENT_ID,lastId)
                startActivity(intent)
            }
            R.id.updateIndonesianOption ->{
                val intent = Intent(this,RTEditorActivity::class.java)
                intent.putExtra(RTEditorActivity.INTENT_ID,lastId)
                startActivityForResult(intent, UPDATE_RESULT)
            }
            R.id.toggleEachLineOption ->{
                if(isWrapped){
                    toggleEachLineItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_sort_white_24))
                }
                else{
                    toggleEachLineItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_wrap_text_white_24))
                }
                EventBus.getDefault().post(ViewEvent(isWrapped))
                isWrapped = !isWrapped
            }
            R.id.toggleIdiomCardOption ->{
                if(isSlideShow){
                    toggleIdiomCardItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_view_comfy_white_24))
                }
                else{
                    toggleIdiomCardItem?.setIcon(ContextCompat.getDrawable(this,R.drawable.baseline_style_white_24))
                }
                EventBus.getDefault().post(IdiomCardViewEvent(isSlideShow))
                isSlideShow = !isSlideShow
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
                Snacky.builder().setActivity(this).setText(getString(R.string.success_update)).success().show()
                val adapter = TranslatedDisplayPagerAdapter(this, supportFragmentManager, lastId)
                translatedDisplayViewPager.offscreenPageLimit = 3
                translatedDisplayViewPager.adapter = adapter
                translatedDisplayTabLayout.setupWithViewPager(translatedDisplayViewPager)
                bookmarkDataEmitter.getEnglishBookmark(lastId,this)

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Snacky.builder().setActivity(this).setText(getString(R.string.failed_update)).success().show()
            }
        }
        if(requestCode == DOCS_VIEW_RESULT){
            if (resultCode == Activity.RESULT_OK) {
                // Snacky.builder().setActivity(this).setText(getString(R.string.success_update)).success().show()
                isTranslationEmpty = false
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Snacky.builder().setActivity(this).setText(getString(R.string.failed_docs)).error().show()
            }
        }
    }

    override fun onErrorShowing() {
        Snacky.builder().setActivity(this).error().setText(getString(R.string.error_translate_service_hasnt_ready)).show()
    }

    fun getTranslation(idiom : String){
        wordClickableService.getIdiomTranslate(idiom)
    }

    fun updateTranslation(translationText : String){
        bookmarkDataEmitter.updateTranslation(translationText,bookmark.id.toString(),this)
    }

    fun refresh(){
        bookmarkDataEmitter.getEnglishBookmark(lastId,this)
    }

    override fun onSuccessUpdatingTranslation()  {
        Snacky.builder().setActivity(this).setText(getString(R.string.success_update)).success().show()
        EventBus.getDefault().post(UpdatedTranslationEvent(true))
    }

    override fun onError(message: String) {
        Snacky.builder().setActivity(this).setText(getString(R.string.failed_update) + " "+message).error().show()
    }

}
