package com.shohiebsense.idiomaticsynonym

import android.app.*
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.MainPagerFragmentStatePagerAdapter
import com.shohiebsense.idiomaticsynonym.view.callbacks.DatabaseCallback
import de.mateware.snacky.Snacky
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.operators.observable.ObservableReplay.observeOn
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), BookmarkDataEmitter.BookmarksCallback, DatabaseCallback{


    lateinit var fragment : Fragment
    lateinit var mainPagerAdapter : MainPagerFragmentStatePagerAdapter
    lateinit var bookmarkEmitter: BookmarkDataEmitter

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
        val INTENT_IS_FROM_UPLOAD_ACTIVITY = "isfromuploadactivity"

    }

    //end toggle


    fun initToolbar(){
        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name) + " - "+ getString(R.string.find_idioms)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtil.unzip(applicationContext)
        setContentView(R.layout.activity_main)
        initToolbar()

        AppUtil.makeDebugLog("heyy "+AppUtil.getFileUploadedNameEvent(this))
        if(AppUtil.isFileUploadedEvent(this) && AppUtil.getFileUploadedNameEvent(this).isNotBlank()){
            Snacky.builder().setActivity(this).success().setText(getString(R.string.success_upload,AppUtil.getFileUploadedNameEvent(this))).show()
            AppUtil.setFileUploadedEvent(this,false)
            AppUtil.setFileUploadedNameEvent(this,"")
        }

        bookmarkEmitter = BookmarkDataEmitter(this)

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        // bottomNavigation.isColored = true;
        mainPagerAdapter = MainPagerFragmentStatePagerAdapter(supportFragmentManager)
        mainViewPager.offscreenPageLimit = 3
        mainViewPager.adapter = mainPagerAdapter
        bottomNavigation.accentColor = Color.parseColor("#f0dfbb")
        bottomNavigation.inactiveColor = Color.parseColor("#fcfcfd")
        bottomNavigation.defaultBackgroundColor = Color.parseColor("#b23a3a")


        val item1 = AHBottomNavigationItem(R.string.text_navigation_do_translation, R.drawable.ic_note_add_white_24dp, R.color.soft_white)
        val item2 = AHBottomNavigationItem(R.string.text_navigation_history, R.drawable.ic_subject_white_18dp, R.color.soft_white)
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
                    0 -> toolbar.title = getString(R.string.app_name) + " - "+ getString(R.string.find_idioms)
                    1 -> toolbar.title = getString(R.string.histories)
                    2 -> toolbar.title = getString(R.string.statistics)
                }
                mainViewPager.currentItem = position
                return true
            }
        })

        if(!AppUtil.getPreDataAskingPreference(this) && AppUtil.getMainGuidance(this) ){
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.ask_pre_data)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.no), dialogClickListener).setNeutralButton(getString(R.string.remind_me_later),dialogClickListener).show()
        }
    }

    var dialogClickListener: DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                AppUtil.setPreDataAskingPreference(this,true)
                Snacky.builder().setActivity(this).info().setText(getString(R.string.loading)).setDuration(Snacky.LENGTH_INDEFINITE).show()
                Completable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                        .subscribe(object : CompletableObserver{
                            override fun onComplete() {
                               recreate()
                            }

                            override fun onSubscribe(d: Disposable) {
                                AppUtil.setIdiomGuidance(this@MainActivity, false)
                                AppUtil.setMainGuidance(this@MainActivity,true)
                                bookmarkEmitter.getPrerequistes(this@MainActivity)
                            }

                            override fun onError(e: Throwable) {
                            }
                        })

            }

            DialogInterface.BUTTON_NEGATIVE -> {
                AppUtil.setPreDataAskingPreference(this,true)
                AppUtil.setIdiomGuidance(this,true)
            }
            DialogInterface.BUTTON_NEUTRAL -> {
                AppUtil.setPreDataAskingPreference(this,false)
            }

        }


    }

    override fun onStart() {
        super.onStart()
        if(TranslatedAndUntranslatedDataEmitter.isIdiomsEmpty()){
            AppUtil.makeErrorLog("yes is empty")
            TranslatedAndUntranslatedDataEmitter(this,this).getAll()
        }
        if(!AppUtil.getIdiomGuidance(this)){
            Snacky.builder().setActivity(this@MainActivity).setText(getString(R.string.pre_requisition_message)).success().setDuration(Snacky.LENGTH_LONG).show()
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
        if(bookmarks.isNotEmpty())
        Log.e("shohiebsenseee ","bookmarks hohoho  "+bookmarks[bookmarks.lastIndex].idioms)

    }

    override fun onError() {
        mainPagerAdapter = MainPagerFragmentStatePagerAdapter(supportFragmentManager)
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