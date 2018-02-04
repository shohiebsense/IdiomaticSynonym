package com.shohiebsense.idiomaticsynonym.view.fragment.home

import android.Manifest
import android.content.Intent
import android.content.res.AssetManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.custom.CustomSnackbar
import com.shohiebsense.idiomaticsynonym.UnderliningActivity
import com.shohiebsense.idiomaticsynonym.services.PdfDisplayerService
import com.shohiebsense.idiomaticsynonym.services.UnderliningService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.CardPagerAdapter
import com.shohiebsense.idiomaticsynonym.view.custom.InputDocumentPageDialogFragment
import com.shohiebsense.idiomaticsynonym.view.callbacks.PdfDisplayCallback
import com.shohiebsense.idiomaticsynonym.view.fragment.UnderliningFragment
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

class PdfDisplayFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener, PdfDisplayCallback,  InputDocumentPageDialogFragment.InputDialogListener {



    lateinit var fetchedText : ArrayList<String>
    lateinit var transitionsContainer : ViewGroup
    var pdfFilePath : String = ""
    var fileName = ""
    var VIEW_STATE = -1





    //REACTIVEXKAN
    override fun loadComplete(nbPages: Int) {
        var TAG = "shohiebsense"
        val meta = uploadPdfView.getDocumentMeta()
        //printBookmarksTree(uploadTextView.getTableOfContents(), "-")
        Log.e(TAG, "title = " + meta.title)
        Log.e(TAG, "author = " + meta.author)
        Log.e(TAG, "subject = " + meta.subject)
        Log.e(TAG, "keywords = " + meta.keywords)
        Log.e(TAG, "creator = " + meta.creator)
        Log.e(TAG, "creationDate = " + meta.creationDate)
        Log.e(TAG, "modDate = " + meta.modDate)
    }


    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    //gettext from pdf turn into reactivex

    lateinit var assetManager: AssetManager
    lateinit var rootDir: File
    lateinit var pdfDisplayerService: PdfDisplayerService
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val PERMISSION_CODE = 42042

    internal var pageNumber: Int = 0
    lateinit var uri : Uri
    var requestPermissions = 999

    lateinit var translateMenuItemn : MenuItem
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

        //AppUtil.isExists(activity, "4.png")
        onTouchTextViewFunctionality()
        /*  AppUtil.makeDebugLog("my Url "+ MediaManager.get().url().generate("2.png"))

          AppUtil.makeDebugLog("loaded")
  */


        //var bitmap = BitmapFactory.decodeFile(AppUtil.getImageString(context, "1.png").absolutePath)

        var imageInSD = ""

        /* try {
             imageInSD = AppUtil.getImageString(context, "1.png").canonicalPath
             AppUtil.makeDebugLog("lolosss")
             val options = BitmapFactory.Options()
             options.inPreferredConfig = Bitmap.Config.ARGB_8888
             var bitmap = BitmapFactory.decodeFile(imageInSD)
             uploadButton.visibility = View.VISIBLE
             uploadButton.setImageBitmap(bitmap)
             } catch(e: Exception) {
                 AppUtil.makeDebugLog (e.toString());
         }*/

        //.setImageDrawable(AppUtil.getFileFromAssets(context))



        var adapter = CardPagerAdapter(activity!!)
        fragmentFetchCardViewPager.adapter = adapter
        //fragmentFetchViewPagerIndicator.setupWithViewPager(fragmentFetchCardViewPager)
        //fragmentFetchViewPagerIndicator.addOnPageChangeListener(mOnPageChangeListener)
        adapter.notifyDataSetChanged()
        performAutomaticSlide()
        AppUtil.makeDebugLog("lhoo ke sini kan?")
    }

    override fun onStart() {
        super.onStart()
        AppUtil.makeDebugLog("isFileNameEmpty "+fileName)
        resumeLoadedPdf()
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
            UnderliningService.ERROR_LOAD -> {
                pdfLoadMenuItem.setVisible(true)
                uploadPdfView.visibility = View.GONE
                //fragmentFetchViewPagerIndicator.visibility = View.VISIBLE
                fragmentFetchCardViewPager.visibility = View.VISIBLE
                return;
            }
            UnderliningService.ERROR_FETCH,
            UnderliningService.ERROR_TRANSLATE -> {
                pdfLoadMenuItem.setVisible(false)
                //fragmentFetchViewPagerIndicator.visibility = View.GONE
                fragmentFetchCardViewPager.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
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
            UnderliningService.STATUS_LOADING -> {
               // AppUtil.makeDebugLog("status loadinggg ")
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
                fragmentFetchCardViewPager.visibility = View.GONE
                //fragmentFetchViewPagerIndicator.visibility = View.GONE
                avLoadingIndicatorView.visibility = View.VISIBLE

            }
            UnderliningService.STATUS_LOADED -> {
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
            }
            UnderliningService.STATUS_RESUMED -> {
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
                pdfLoadMenuItem.isVisible = true
                activity!!.title = getString(R.string.app_name)
                activity!!.invalidateOptionsMenu()
            }
            UnderliningService.STATUS_FETCHED -> {
                uploadPdfView.visibility = View.GONE
                textFetchedScrollView.visibility = View.VISIBLE
            }
            UnderliningService.STATUS_INIT -> {
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
                fragmentFetchCardViewPager.visibility = View.VISIBLE
                //fragmentFetchViewPagerIndicator.visibility = View.VISIBLE
                avLoadingIndicatorView.visibility = View.GONE
            }

            UnderliningService.STATUS_FETCHED_DB -> {

            }
            UnderliningService.STATUS_TRANSLATED -> {

            }
            UnderliningService.STATUS_COMPLETED -> {

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




    fun getFileName(uri: Uri): String {
        var result: String = ""
        if (uri.scheme == "content") {
            val cursor = context!!.getContentResolver().query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result
    }


    fun getPath(uri: Uri): String {

        var path: String? = null
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        val cursor = context!!.contentResolver.query(uri, projection, null, null, null)

        if (cursor == null) {
            path = uri.path
        } else {
            cursor.moveToFirst()
            val column_index = cursor.getColumnIndexOrThrow(projection[0])
            path = cursor.getString(column_index)
            cursor.close()
        }

        return if (path == null || path.isEmpty()) uri.path else path
    }



    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_fetch_fragment, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        pdfLoadMenuItem = menu.findItem(R.id.loadPdfMenuOptions)!!
        translateMenuItemn = menu.findItem(R.id.translateTextMenuOptions)!!

        when(VIEW_STATE){
            -1 -> {
                pdfLoadMenuItem.setVisible(true)
                translateMenuItemn.setVisible(false)
            }
            UnderliningService.STATUS_LOADED -> {
                pdfLoadMenuItem.setVisible(false)
                translateMenuItemn.setVisible(true)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.translateTextMenuOptions -> {
                showInputDialog()
            }
            R.id.loadPdfMenuOptions -> {
                pdfDisplayerService.promptLoadPdf()
            }

        }
        return true
    }


    private fun showPhoneStatePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
                context!!, Manifest.permission.READ_PHONE_STATE)
        //AppUtil.makeDebugLog("is Permission number "+permissionCheck)

    }

    override fun onLoadingPdf() {
        toggleViews(UnderliningService.STATUS_LOADING)
    }

    fun resumeLoadedPdf(){
        if(!fileName.isBlank()){
            //onFinishedLoadingPdf(pdfFilePath)
            VIEW_STATE= UnderliningService.STATUS_RESUMED
            toggleViews(VIEW_STATE)
        }
    }

    override fun onFinishedLoadingPdf(fileString : String) {
        pdfDisplayerService.pdfFile = File(fileString)
        pdfLoadMenuItem.setVisible(false)
        uploadPdfView.fromFile(pdfDisplayerService.pdfFile)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(activity))
                .spacing(10) // in dp
                .load()
        VIEW_STATE = UnderliningService.STATUS_LOADED
        toggleViews(VIEW_STATE)
        translateMenuItemn.setVisible(true)
        this.fileName = fileString
        activity!!.invalidateOptionsMenu()
    }

    fun saveStateFileName(fileString: String) : Bundle {
        val bundle = Bundle()
        return bundle
    }

    override fun onFetchingPdf() {
        toggleViews(UnderliningService.STATUS_LOADING)

    }



    override fun onErrorLoadingPdf() {
        toggleErrorViews(UnderliningService.ERROR_LOAD)
    }


    override fun onFinishedFetchingPdfAsList(fetchedText: MutableList<String>, name: String) {
        this.fetchedText = fetchedText as ArrayList<String>
        //AppUtil.makeDebugLog("casting mutable to arraylist succeed with size "+this.fetchedText.size)
        // textFetchedTextView.setText(extractedPdfTexts)
        toggleViews(UnderliningService.STATUS_FETCHED)
        var intent = Intent(activity, UnderliningActivity::class.java)
        var fetchedTextAsList = ArrayList<String>()
        intent.putExtra(UnderliningActivity.INTENT_MESSAGE, UnderliningFragment::class.java.name)
        intent.putExtra(UnderliningActivity.INTENT_FILENAME, name)
        fetchedTextAsList.addAll(this.fetchedText)
        intent.putExtra(UnderliningActivity.INTENT_FETCHED_TEXT, fetchedTextAsList)
        AppUtil.makeDebugLog("FINISHED FETCHING PDF WITH SIZE "+fetchedText.size)
        startActivity(intent)
    }

    override fun onFinishedFetchingPdf(fetchedText: String, name: String) {
        toggleViews(UnderliningService.STATUS_FETCHED)
        var intent = Intent(activity, UnderliningActivity::class.java)
        intent.putExtra(UnderliningActivity.INTENT_MESSAGE, UnderliningFragment::class.java.name)
        intent.putExtra(UnderliningActivity.INTENT_FILENAME, name)
        AppUtil.putStringToPreferences(activity!!, fetchedText)
        startActivity(intent)
    }



    fun showInputDialog(){
        var dialog = InputDocumentPageDialogFragment()
        dialog.setTargetFragment(this, 1)
        dialog.show(fragmentManager, InputDocumentPageDialogFragment::class.java.simpleName)

    }

    override fun onDialogPositiveClick(number: Int) {
        activity!!.setTitle("Loading ...")
        avLoadingIndicatorView.visibility = View.VISIBLE
        CustomSnackbar.make(view!!.parent as ViewGroup, CustomSnackbar.LENGTH_INDEFINITE).setText("Please Wait ").hidePermissionAction().show()
        pdfDisplayerService.fetchText(number)
    }


    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {

        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }






}
