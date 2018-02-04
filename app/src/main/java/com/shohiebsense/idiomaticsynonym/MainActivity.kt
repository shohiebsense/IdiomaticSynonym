package com.shohiebsense.idiomaticsynonym

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.view.WindowManager
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.*
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.DatabaseCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dialog_input_document_page.*

class MainActivity : AppCompatActivity(), BookmarkDataEmitter.BookmarksCallback, DatabaseCallback {


    val NAVIGATION_TRANSLATE = 0
    val NAVIGATION_BOOKMARKS = 1
    val NAVIGATION_STATISTCS = 2

    lateinit var fragment : Fragment
     var bookmarks = arrayListOf<BookmarkedEnglish>()
    lateinit var mainPagerAdapter : MainPagerFragmentStatePagerAdapter

    //toggle

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mainViewPager.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    private val mShowPart2Runnable = Runnable {
        // Delayed display of UI elements
       supportActionBar!!.show()
    }

    private var mVisible: Boolean = true
    private val mHideRunnable = Runnable { hide() }
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val mDelayHideTouchListener = View.OnTouchListener { _, _ ->
        if (AUTO_HIDE) {
            delayedHide(AUTO_HIDE_DELAY_MILLIS)
        }
        false
    }

    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }


    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }

    //end toggle



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtil.unzip(applicationContext)
        setContentView(R.layout.activity_main)
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val bookmarkEmitter = BookmarkDataEmitter(this)
        bookmarkEmitter.insertBookmarkEnglish("fileName","whoooolleee text","hiii")
        bookmarkEmitter.getEnglisbBookmarks(this)
        val lastId = bookmarkEmitter.getLastInsertedIdFromBookmarkedEnglish()
        AppUtil.makeDebugLog("last id "+lastId)
        // bottomNavigation.isColored = true;

        bottomNavigation.accentColor = Color.parseColor("#f0dfbb")
        bottomNavigation.inactiveColor = Color.parseColor("#fcfcfd")
        bottomNavigation.defaultBackgroundColor = Color.parseColor("#b23a3a")


        val item1 = AHBottomNavigationItem(R.string.text_navigation_do_translation, R.drawable.ic_note_add_white_24dp, R.color.soft_white)
        val item2 = AHBottomNavigationItem(R.string.text_navigation_bookmarks, R.drawable.ic_subject_white_18dp, R.color.soft_white)
        val item3 = AHBottomNavigationItem(R.string.text_navigation_statistics, R.drawable.ic_subject_white_18dp, R.color.soft_white)
       // item1.setColorRes(R.color.soft_white)
        bottomNavigation.isTranslucentNavigationEnabled = false
        bottomNavigation.addItem(item1)
        bottomNavigation.addItem(item2)
        bottomNavigation.addItem(item3)
        bottomNavigation.setOnTabSelectedListener(object : AHBottomNavigation.OnTabSelectedListener{
            override fun onTabSelected(position: Int, wasSelected: Boolean): Boolean {
                if(!wasSelected){
                    mainViewPager.currentItem = 0
                }
                when(position){
                    0 -> title = getString(R.string.app_name)
                    1 -> title = "Bookmarks"
                    2 -> title = "Statistics"
                }
                mainViewPager.currentItem = position
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if(TranslatedAndUntranslatedDataEmitter.isIdiomsEmpty()){
            TranslatedAndUntranslatedDataEmitter(this,this).getAll()
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }


    fun addFragment(fragment : Fragment, frameId : Int){
        fragmentManager.inTransaction { add(frameId, fragment) }
    }



    override fun onFetched(bookmarks: ArrayList<BookmarkedEnglish>) {
        this.bookmarks.addAll(bookmarks)
        mainPagerAdapter = MainPagerFragmentStatePagerAdapter(supportFragmentManager, bookmarks)
        mainViewPager.offscreenPageLimit = 3
        mainViewPager.adapter = mainPagerAdapter
    }

    override fun onError() {
        mainPagerAdapter = MainPagerFragmentStatePagerAdapter(supportFragmentManager, bookmarks)
        mainViewPager.offscreenPageLimit = 3
        mainViewPager.adapter = mainPagerAdapter
    }



    /**
     * database callbacks
     */

    override fun onFetchingData(idiomMode: Int) {

    }

    override fun onErrorFetchingData() {
    }

    override fun onFetchedTranslatedData() {
    }

    override fun onFetchedUntranslatedData() {
    }

    override fun onFetchedBoth() {
    }

    /**
     * end database callbacks
     */

    private fun toggle() : Boolean{
        if (mVisible) {
            hide()
        } else {
            show()
        }
        return true
    }

    private fun hide() {
        // Hide UI first
        supportActionBar!!.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable)
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        mainViewPager.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable)
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }




}