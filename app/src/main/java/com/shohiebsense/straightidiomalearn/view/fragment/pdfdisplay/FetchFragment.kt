package com.shohiebsense.straightidiomalearn.view.fragment.pdfdisplay

import android.Manifest
import android.app.Fragment
import android.content.res.AssetManager
import android.os.Bundle
import android.util.Log
import com.shohiebsense.straightidiomalearn.R
import kotlinx.android.synthetic.main.fragment_fetch.*
import android.content.Intent
import android.app.Activity.RESULT_OK
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.content.ContextCompat
import android.view.*
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.shohiebsense.straightidiomalearn.MainActivity
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.services.pdfFetchers.PdfFetcher
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchCallback
import com.shohiebsense.straightidiomalearn.view.fragment.fetchedtextdisplay.FetchedTextFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.io.*
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

class FetchFragment : Fragment(), OnPageChangeListener, OnLoadCompleteListener, FetchCallback {

    lateinit var fetchedText : ArrayList<String>

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
    lateinit var pdfFetcher : PdfFetcher
    val READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val PERMISSION_CODE = 42042

    internal var pageNumber: Int = 0
    lateinit var uri : Uri
    var requestPermissions = 999

    lateinit var menuItem : MenuItem


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //AppUtil.makeErrorLog("sizee sampe fetched translatedIdiom "+ DatabaseDataEmitter.translatedIdiomList.size )
        //AppUtil.makeErrorLog("sizee sampe fetched UNTRANSLATED "+ DatabaseDataEmitter.untranslatedIdiomList.size )

        setHasOptionsMenu(true)
        pdfFetcher = PdfFetcher(activity, this).init()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_fetch, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //logic goes here

        showPhoneStatePermission()

        uploadButton.setOnClickListener{
            //AppUtil.navigateToFragment(context, LoadFragment::class.java.name)

            pdfFetcher.promptLoadPdf()
        }
        onTouchTextViewFunctionality()
    }

    override fun onStart() {
        super.onStart()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            7 -> if (resultCode == RESULT_OK) {

            }
        }
    }





    fun onTouchTextViewFunctionality(){
        textFetchedTextView.setOnTouchListener{ view: View, motionEvent: MotionEvent ->

            scrollAfterTouched()
            true
        }
    }


    //reactivies it


    fun fadeViewsAfterFetched(){
        uploadButton.visibility = View.GONE
        uploadPdfView.visibility = View.GONE
    }

    fun toggleUploadButtonView(show : Boolean){
        if(show){
            uploadButton.visibility = View.VISIBLE

        }
        else{

        }
    }

    fun toggleErrorViews(ERROR_NO : Int){
        avLoadingIndicatorView.visibility = View.GONE
        when(ERROR_NO){
            PdfFetcher.ERROR_LOAD -> {
                uploadButton.visibility = View.VISIBLE
                uploadPdfView.visibility = View.GONE
                return;
            }
            PdfFetcher.ERROR_FETCH,
                    PdfFetcher.ERROR_TRANSLATE -> {
                uploadButton.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
                return
            }

        }
        //bikin error view

    }


    fun toggleViews(STATUS : Int){

        uploadButton.visibility = View.GONE
        AppUtil.makeDebugLog("view toggled ")

        when(STATUS){
            PdfFetcher.STATUS_LOADING -> {
                AppUtil.makeDebugLog("status loadinggg ")
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.GONE
                avLoadingIndicatorView.visibility = View.VISIBLE
            }
            PdfFetcher.STATUS_LOADED -> {
                textFetchedScrollView.visibility = View.GONE
                uploadPdfView.visibility = View.VISIBLE
            }
            PdfFetcher.STATUS_FETCHED -> {
                uploadPdfView.visibility = View.GONE
                textFetchedScrollView.visibility = View.VISIBLE
            }

            PdfFetcher.STATUS_FETCHED_DB -> {

            }
            PdfFetcher.STATUS_TRANSLATED -> {

            }
            PdfFetcher.STATUS_COMPLETED -> {

            }
        }

    }

    fun scrollAfterTouched(){
        Observable.just(textFetchedScrollView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {textFetchedScrollView ->
                    textFetchedScrollView.scrollTo(textFetchedScrollView.scrollX,textFetchedTextView.bottom + AppUtil.getHeightOfWindow(activity))
                }

    }




    fun getFileName(uri: Uri): String {
        var result: String = ""
        if (uri.scheme == "content") {
            val cursor = activity.getContentResolver().query(uri, null, null, null, null)
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
        val cursor = activity.contentResolver.query(uri, projection, null, null, null)

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

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menuItem = menu?.findItem(R.id.fetchMenuOptions)!!.setVisible(false)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.fetchMenuOptions ->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    AppUtil.makeDebugLog("ewww")

                }
                //toggleViews(false,true,true)
            }
            R.id.translateTextMenuOptions -> {

                pdfFetcher.fetchText()
            }

        }
        return true
    }

    private fun showPhoneStatePermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_PHONE_STATE)
        AppUtil.makeDebugLog("apakah "+permissionCheck)

    }

    override fun onLoadingPdf() {
        toggleViews(PdfFetcher.STATUS_LOADING)
    }

    override fun onFinishedLoadingPdf(file : File) {
        uploadButton.visibility = View.GONE

        AppUtil.makeDebugLog("pdfFileNamee "+file.name)
        uploadPdfView.fromFile(file)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(DefaultScrollHandle(activity))
                .spacing(10) // in dp
                .load()
        toggleViews(PdfFetcher.STATUS_LOADED)

    }

    override fun onFetchingPdf() {
        toggleViews(PdfFetcher.STATUS_LOADING)

    }



    override fun onErrorLoadingPdf() {
        toggleErrorViews(PdfFetcher.ERROR_LOAD)
    }


    override fun onFinishedFetchingPdf(fetchedText : MutableList<String>) {
        this.fetchedText = fetchedText as ArrayList<String>
        AppUtil.makeDebugLog("casting mutable to arraylist succeed")
       // textFetchedTextView.setText(fetchedText)
        toggleViews(PdfFetcher.STATUS_FETCHED)
        var intent = Intent(activity, MainActivity::class.java)
        intent.putExtra(MainActivity.intentMessage, FetchedTextFragment::class.java.name)
        var fetchedTextAsList = ArrayList<String>()
        fetchedTextAsList.addAll(this.fetchedText)
        intent.putExtra(MainActivity.fetchedTextMessage, fetchedTextAsList)
        startActivity(intent)
    }



}
