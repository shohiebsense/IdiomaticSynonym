package com.shohiebsense.idiomaticsynonym.view.activity.home.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.activity.setting.SettingsActivity
import com.shohiebsense.idiomaticsynonym.view.activity.underlining.UnderliningActivity
import com.shohiebsense.idiomaticsynonym.services.PdfDisplayerService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.CardPagerAdapter
import com.shohiebsense.idiomaticsynonym.view.custom.InputDocumentPageDialogFragment
import com.shohiebsense.idiomaticsynonym.view.callbacks.PdfDisplayCallback
import com.shohiebsense.idiomaticsynonym.view.activity.underlining.fragment.UnderliningFragment
import de.mateware.snacky.Snacky
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_pdfdisplay.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

//WARNINGGGGGG 5 NOVEMBER EXCEPTION USER RATE TRANSLATION EXCEEDED

/**
 * Created by Shohiebsense on 07/09/2017.
 *
 *
 * next, error handle
 * if pdf, only pdf
 * if the content dominant with english
 * sejauh ini ok, hanya salah dokument
 *
 * advanced : search the phrase
 *
 * next -> perbaikan layout, kasih scroll
 *
 * beres
 *
 * next -> search term, search related...
 *
 * kateglo ieu mah
 *
 * kateglo beres
 *
 * 1. Proses translate lama, kasih selingan apa
 * 2. Langsung query db, cocokin
 */

class PdfDisplayFragment : Fragment(), PdfDisplayCallback,  InputDocumentPageDialogFragment.InputDialogListener {



    lateinit var fetchedText : ArrayList<String>
    lateinit var transitionsContainer : ViewGroup
    var pageCount : Int = 0
    var fileName = ""
    var VIEW_STATE = -1

    val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1




    //REACTIVEXKAN


    //gettext from pdf turn into reactivex

    lateinit var assetManager: AssetManager
    lateinit var rootDir: File
    lateinit var pdfDisplayerService: PdfDisplayerService
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val PERMISSION_CODE = 42042

    internal var pageNumber: Int = 0
    lateinit var uri : Uri
    var requestPermissions = 999

    lateinit var translateMenuItem : MenuItem
    lateinit var pdfLoadMenuItem: MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppUtil.makeErrorLog("sizee sampe fetched translatedIdiom "+ TranslatedAndUntranslatedDataEmitter.translatedIdiomList.size )
        //AppUtil.makeErrorLog("sizee sampe fetched UNTRANSLATED "+ TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.size )
        setHasOptionsMenu(true)
        pdfDisplayerService = PdfDisplayerService(context!!, this).init()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pdfdisplay, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the user interaction to manually show or hide the system UI.
        //logic goes here
        // showPhoneStatePermission()
        transitionsContainer = rootConstraintLayout as ViewGroup



        var listener: RequestListener<Drawable> = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                AppUtil.makeErrorLog(e.toString() + " failed")
                avLoadingIndicatorView.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                avLoadingIndicatorView.visibility = View.GONE
                return true
            }

        }
        onTouchTextViewFunctionality()
        var adapter = CardPagerAdapter(activity!!)
        fragmentFetchCardViewPager.adapter = adapter
        adapter.notifyDataSetChanged()
        performAutomaticSlide()
        AppUtil.makeDebugLog("lhoo ke sini kan?")
    }

    override fun onStart() {
        super.onStart()
    }

    var currentPage = 0
    var timer: Timer? = null
    val DELAY_MS: Long = 500//delay in milliseconds before task is to be executed
    val PERIOD_MS: Long = 3000 // time in milliseconds between successive task executions.


    fun performAutomaticSlide(){
        var handler = Handler()
        var slideRunnable = Runnable {
            if (currentPage == 15 -1){
                currentPage = 0
            }
            if(fragmentFetchCardViewPager != null)
            fragmentFetchCardViewPager.setCurrentItem(currentPage++,true)
        }

        slideRunnable.run()


        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler.post(slideRunnable)
            }

        }, 100, 5000)
    }

    fun onTouchTextViewFunctionality(){
        textLoadedTextView.setOnTouchListener{ view: View, motionEvent: MotionEvent ->

            scrollAfterTouched()
            true
        }
    }

    //reactivies it
    fun toggleErrorViews(ERROR_NO : Int){
        TransitionManager.beginDelayedTransition(transitionsContainer)
        avLoadingIndicatorView.visibility = View.GONE
        when(ERROR_NO){
            PdfDisplayerService.ERROR_LOAD -> {
                pdfLoadMenuItem.setVisible(true)
                //fragmentFetchViewPagerIndicator.visibility = View.VISIBLE
                fragmentFetchCardViewPager.visibility = View.VISIBLE
                return;
            }
            PdfDisplayerService.ERROR_FETCH,
            PdfDisplayerService.ERROR_TRANSLATE -> {
                pdfLoadMenuItem.setVisible(false)
                //fragmentFetchViewPagerIndicator.visibility = View.GONE
                fragmentFetchCardViewPager.visibility = View.GONE
                return
            }

        }
        //bikin error factCardView

    }


    fun toggleViews(STATUS : Int){
        TransitionManager.beginDelayedTransition(transitionsContainer)
        pdfLoadMenuItem.isVisible = false
        //AppUtil.makeDebugLog("factCardView toggled ")

        when(STATUS){

            PdfDisplayerService.STATUS_FETCHED -> {
                textFetchedScrollView.visibility = View.VISIBLE
            }
            PdfDisplayerService.STATUS_INIT -> {
                textFetchedScrollView.visibility = View.GONE
                fragmentFetchCardViewPager.visibility = View.VISIBLE
                //fragmentFetchViewPagerIndicator.visibility = View.VISIBLE
                avLoadingIndicatorView.visibility = View.GONE
            }

            PdfDisplayerService.STATUS_FETCHED_DB -> {

            }
            PdfDisplayerService.STATUS_TRANSLATED -> {

            }
            PdfDisplayerService.STATUS_COMPLETED -> {

            }
        }

    }

    fun scrollAfterTouched(){
        Observable.just(textFetchedScrollView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {textFetchedScrollView ->
                    textFetchedScrollView.scrollTo(textFetchedScrollView.scrollX, textLoadedTextView.bottom + AppUtil.getHeightOfWindow(activity!!))
                }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_fetch_fragment, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        pdfLoadMenuItem = menu.findItem(R.id.loadPdfMenuOptions)!!
        translateMenuItem = menu.findItem(R.id.translateTextMenuOptions)!!

        when(VIEW_STATE){
            -1 -> {
                pdfLoadMenuItem.setVisible(true)
                translateMenuItem.setVisible(false)
            }
        }

    }

    fun loadPdf() : Boolean {
        return checkFilePermission()
    }

    fun checkFilePermission() : Boolean{
        AppUtil.makeErrorLog("ke sini kan??")
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity?.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    activity?.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pdfDisplayerService.promptLoadPdfDialog()
                return true
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                return false
            }
        } else {
            pdfDisplayerService.promptLoadPdfDialog()
            return true
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.translateTextMenuOptions -> {
                performFetchingTextDialog()
            }
            R.id.loadPdfMenuOptions -> {
                loadPdf()
            }
            R.id.settingMenuOption -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == ASK_MULTIPLE_PERMISSION_REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pdfDisplayerService.promptLoadPdfDialog()
            } else {
                AppUtil.makeErrorLog("deniedd")
                if(activity != null)
                AppUtil.showSnackbar(activity as Activity,AppUtil.SNACKY_WARNING,getString(R.string.permission_required))
            }
        }
    }





    private fun showPhoneStatePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
                context!!, Manifest.permission.READ_PHONE_STATE)
        //AppUtil.makeDebugLog("is Permission number "+permissionCheck)

    }

    override fun onLoadingPdf() {
    }


    override fun onFinishedLoadingPdf() {
        pdfLoadMenuItem.setVisible(false)
        pageCount = pdfDisplayerService.loadedDocument.numberOfPages
        translateMenuItem.setVisible(true)
        this.fileName = AppUtil.getOnlyFileName(pdfDisplayerService.pdfFile.name)
        activity!!.invalidateOptionsMenu()
        performFetchingTextDialog()
    }

    fun saveStateFileName(fileString: String) : Bundle {
        val bundle = Bundle()
        return bundle
    }

    override fun onProcess() {

    }



    override fun onError() {
        toggleErrorViews(PdfDisplayerService.ERROR_LOAD)
    }


    /**
     * optional, hence not ran
     */
    override fun onFinishedFetchingPdfAsList(fetchedText: MutableList<String>, name: String) {
        this.fetchedText = fetchedText as ArrayList<String>
        //AppUtil.makeDebugLog("casting mutable to arraylist succeed with size "+this.fetchedText.size)
        // textFetchedTextView.setText(extractedPdfTexts)
        var intent = Intent(activity, UnderliningActivity::class.java)
        var fetchedTextAsList = ArrayList<String>()
        intent.putExtra(UnderliningActivity.INTENT_MESSAGE, UnderliningFragment::class.java.name)
        intent.putExtra(UnderliningActivity.INTENT_FILENAME, name)
        fetchedTextAsList.addAll(this.fetchedText)

        //UNCOMMENT AGAIN
        BookmarkDataEmitter(context!!).insertBookmarkEnglish(fileName, "","")

        AppUtil.makeDebugLog("FINISHED FETCHING PDF WITH SIZE "+fetchedText.size)
        startActivity(intent)
    }

    override fun onEmitted(fetchedText: String, name: String) {
        toggleViews(PdfDisplayerService.STATUS_FETCHED)
        var intent = Intent(activity, UnderliningActivity::class.java)
        val bookmarkEmitter = BookmarkDataEmitter(context!!)
        val lastId = bookmarkEmitter.insertBookmarkEnglish(fileName, fetchedText,"")
        intent.putExtra(UnderliningActivity.INTENT_MESSAGE, UnderliningFragment::class.java.name)
        intent.putExtra(UnderliningActivity.INTENT_FILENAME, name)
        intent.putExtra(UnderliningActivity.INTENT_ID, lastId)
        startActivity(intent)
    }



    fun performFetchingTextDialog(){

        var dialog = InputDocumentPageDialogFragment.newInstance(pageCount, pdfDisplayerService.pdfFile.name)
        dialog.setTargetFragment(this, 1)
        dialog.show(fragmentManager, InputDocumentPageDialogFragment::class.java.simpleName)

    }

    override fun performFetchingText(from: Int, to : Int) {
        if(activity != null){
            AppUtil.showSnackbar(activity as Activity,AppUtil.SNACKY_INFO,getString(R.string.loading),Snacky.LENGTH_INDEFINITE)
        }
        avLoadingIndicatorView.visibility = View.VISIBLE
        pdfDisplayerService.fetchText(from,to)
    }


    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }

    override fun onErrorReadingFile() {
        if(activity != null)
        AppUtil.showSnackbar(activity as Activity, AppUtil.SNACKY_WARNING,getString(R.string.text_not_valid_pdf))
    }






}
