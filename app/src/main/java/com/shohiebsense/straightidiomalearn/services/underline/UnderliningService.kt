package com.shohiebsense.straightidiomalearn.services.underline

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import com.eaio.stringsearch.BNDMCI
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.TranslateService
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchCallback
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.io.*
import java.util.*

/**
 * Created by Shohiebsense on 09/09/2017.
 *
 * punya kemampuan OCR -> read image to txt
 *
 *
 *
 *
 * MAKSIMAL 14 HALAMAN
 * KASIH IPS, 14 HALAMAN 14 HALAMAN JANGAN SEKALIGUS
 */

/*
class UnderliningService(val context : Context, val uri : Uri) {
*/
class UnderliningService constructor (val activity : Context) {

    lateinit var inputStream : InputStream
    var pdfValid = false
    lateinit var destinationFile : File
    lateinit var fetchedText : String
    lateinit var pdfFileName: String
    var translatedText : String = ""
    var translatedIdiomText : String = ""
    // lateinit var translatedIdiomList: MutableList<TranslatedIdiom>
    // lateinit var untranslatedIdiomList: MutableList<UntranslatedIdiom>
    lateinit var callback : FetchCallback
    lateinit var fetchedTextCallback : FetchedTextCallback
    var fetchedPdfText = mutableListOf<String>()
    var translatedFetchedPdfText = mutableListOf<String>()
    var fetchedUntranslatedIdiomTexts = mutableListOf<String>()
    var fetchedTranslatedIdiomTexts = mutableListOf<String>()
    var fetchedIdiomsMeaning = mutableListOf<String>()
    var translateService : TranslateService

    init {
        translateService = TranslateService(activity)
    }
/*
       JADI GINI, BAWA HASIL IDIOM DALAM BENTUK ARRAYLIST
       HASIL IDIOM DIDAPAT KETIKA MENCOCOKAN TEKS (FETCHEDTEXT) DENGAN IDIOM YANG TERSEDIA (DB)
       POKOKNYA TRANSLASIKAN DULU FETCHED TEXT

       TRANSLASIKAN IDIOM
       BAWA FETCHEDTEXT YANG MERUPAKAN HASIL FETCHED DARI PDF COCOKAN DENGAN TRANSLASI
       TIDAK PERLU TRANSLASI BERSAMAAN, CUKUP YANG DI KLIK AJA.



 */

    constructor(activity: Context, callback : FetchCallback) : this(activity) {
        this.callback = callback
    }

    constructor(activity: Context, fetchedTextCallback: FetchedTextCallback) : this(activity){
        this.fetchedTextCallback = fetchedTextCallback
    }


    var separator = File.separator
    var anuu = Environment.getExternalStorageDirectory()
    var rootDirectory : String = "$anuu$separator"

    companion object {
        val STATUS_LOADING = 5
        val ERROR_LOAD = 0
        val ERROR_FETCH = 1
        val ERROR_TRANSLATE = 2
        val STATUS_LOADED = 10
        val STATUS_FETCHED = 9
        val STATUS_TRANSLATED = 8
        val STATUS_FETCHED_DB = 7
        val STATUS_COMPLETED = 6
        val STATUS_INIT = 3


        //cara1
        @Throws(Exception::class)
        fun testPDFBoxExtractImages() {
            val document = PDDocument.load(File("D:/Temp/Test.pdf"))
            val list = document.pages
            for (page in list) {
                val pdResources = page.resources
                for (c in pdResources.xObjectNames) {
                    val o = pdResources.getXObject(c)
                    if (o is PDImageXObject) {
                        val file = File("D:/Temp/" + System.nanoTime() + ".png")
                        //create file
                        //ImageIO.write(((PDImageXObject)o).getImage(), "png", file);
                    }
                }
            }
            document.close()
        }


    }

    fun init() : UnderliningService {
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }



    //method2 cara 2
    @Throws(IOException::class)
    fun getImagesFromPDF(document: PDDocument): List<Bitmap> {
        val images = ArrayList<Bitmap>()
        for (page in document.pages) {
            images.addAll(getImagesFromResources(page.resources))
        }

        return images
    }

    @Throws(IOException::class)
    private fun getImagesFromResources(resources: PDResources): List<Bitmap> {
        val images = ArrayList<Bitmap>()

        for (xObjectName in resources.xObjectNames) {
            val xObject = resources.getXObject(xObjectName)

            if (xObject is PDFormXObject) {
                images.addAll(getImagesFromResources(xObject.resources))
            } else if (xObject is PDImageXObject) {
                images.add(xObject.image)
            }
        }

        return images
    }

    fun translate(fetched : ArrayList<String>){
        fetchedPdfText = fetched
        var observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                fetchedTextCallback.onTranslatingText()
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed translate with size "+translatedFetchedPdfText.size)

                fetchedTextCallback.onFinishedTranslatingText()
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error translating pertam ")
                AppUtil.makeErrorLog(e.toString())
                fetchedTextCallback.onErrorTranslatingText()
            }

            override fun onNext(t: String) {
                //AppUtil.makeDebugLog("translated text :\n"+t)
                translatedFetchedPdfText.add(t)
            }

        }
        // TranslateService().translate(activity, observer, fetched)
        //TranslateService().translate(activity, observer, "the quick brown fox jumps over the lazy dog. They are dogs and human")


        fetched.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe{
            translateService.translate(observer, it)
        }
    }

    fun translateIdiom(fetched : String){
        var observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                //process startup
            }

            override fun onComplete() {
                fetchedTextCallback.onClickedIdiomText(translatedIdiomText)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog(e.toString())
                fetchedTextCallback.onErrorClickedIdiomText()
            }

            override fun onNext(t: String) {
                AppUtil.makeDebugLog(t)
                translatedIdiomText = t
            }

        }
         translateService.translate(observer, fetched)
    }

    fun getUnderlineTheFetchedText(fetchedText : ArrayList<String>){

        //change to arraylist ...
        fetchedPdfText = fetchedText
        var index = 0
        var anu = mutableListOf<String>()
        fetchedText.forEach {
            singleString ->
            anu.add(AppUtil.newSpaceInString(singleString))
        }



        anu.toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(){
                    anusingle ->

                    AppUtil.makeDebugLog("== BEGIN UNDERLINING == ")
                    var decoratedSpan = SpannableString(anusingle)


                    val translatedObserver = object : Observer<TranslatedIdiom> {
                        override fun onSubscribe(d: Disposable) {
                            AppUtil.makeDebugLog("disposing "+d.isDisposed)
                            fetchedTextCallback.onFindingTranslatedIdiom()
                        }


                        override fun onComplete() {
                            AppUtil.makeDebugLog("== finished underlining: TRANSLATED ==")
                            fetchedTextCallback.onFinishedFindingTranslatedIdiom(anusingle, decoratedSpan)
                        }

                        override fun onError(e: Throwable) {
                            AppUtil.makeDebugLog("error underlining with exception: "+e.toString())
                            fetchedTextCallback.onErrorUnderliningText(decoratedSpan)
                        }



                        override fun onNext(translatedIdiom: TranslatedIdiom) {

                            AppUtil.makeDebugLog("==== onNextt ==== ")
                            AppUtil.makeErrorLog( "nomorr "+index)
                            index = index++
                            var index = BNDMCI().searchString(anusingle, translatedIdiom.idiom)
                            var spaceIndex = BNDMCI().searchString(anusingle, " ")
                            if(index != -1 && spaceIndex != -1){
                                //fetchedPdfText.get(index)
                                fetchedTranslatedIdiomTexts.add(translatedIdiom.meaning)
                                underliningIdiomProcess(index, decoratedSpan, translatedIdiom)
                            }
                        }
                    }

                    if(DatabaseDataEmitter.untranslatedIdiomList.isEmpty() || DatabaseDataEmitter.translatedIdiomList.isEmpty() ){
                        AppUtil.makeErrorLog("error size list 0")
                        fetchedTextCallback.onErrorUnderliningText(decoratedSpan);
                    }
                   /* if(translatedFetchedPdfText.isEmpty() ||translatedFetchedPdfText.size == 0){
                        AppUtil.makeErrorLog("translate-an kosong")
                        fetchedTextCallback.onErrorUnderliningText(decoratedSpan);
                    }*/



                    // DatabaseDataEmitter.translatedIdiomList.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe(translatedObserver)
                    DatabaseDataEmitter.translatedIdiomList.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(translatedObserver)



                    getUnderlineFetchedTextUsingUntranslatedData(anusingle, decoratedSpan)
                        //translatedObserver

                    //DatabaseDataEmitter.translatedIdiomList.toFlowable().subscribeOn(Schedulers.io()).filter{aduh -> BNDMCI().searchString(anusingle, aduh.idiom)  != -1 }.observeOn(Schedulers.io()).subscribe(translatedObserver)

                    //getUnderlineTheFetchedWithUntranslatedText(anusingle, )

                    //commented for development purposes
                }
    }

    fun getUnderlineFetchedTextUsingUntranslatedData(anusingle : String, decoratedSpan: SpannableString) {
        var untranslatedObserver = object : Subscriber<UntranslatedIdiom> {
            override fun onSubscribe(s: Subscription?) {
            }

            override fun onComplete() {
                fetchedTextCallback.onFinishedUntranslatedIdiom()

            }

            override fun onNext(t: UntranslatedIdiom) {
                var index = BNDMCI().searchString(anusingle,t.idiom)
                fetchedUntranslatedIdiomTexts.add(t.idiom)
                AppUtil.makeErrorLog( "nomorr untranslated "+index)
                underliningIdiomProcess(index, decoratedSpan, t)
            }

            override fun onError(t: Throwable?) {

            }
        }
        DatabaseDataEmitter.untranslatedIdiomList.toFlowable().subscribeOn(Schedulers.io()).filter{aduh -> BNDMCI().searchString(anusingle, aduh.idiom)  != -1 }.observeOn(Schedulers.io()).subscribe(untranslatedObserver)

    }



    //Untranslated
    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableString, untranslatedIdiom: UntranslatedIdiom) {
        underliningSpannableString(index,decoratedSpan, untranslatedIdiom.idiom, "")
    }

    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableString, translatedIdiom: TranslatedIdiom){
        underliningSpannableString(index,decoratedSpan, translatedIdiom.idiom,translatedIdiom.meaning)

    }

    fun underliningSpannableString(index : Int, decoratedSpan : SpannableString, idiom : String, meaning : String) : SpannableString{
        decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.secondaryDarkColor)), 0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index,index+idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        Toast.makeText(activity, "underlining list: "+meaning, Toast.LENGTH_LONG).show()

        var clickableSpan = object : ClickableSpan()
        {
            override fun onClick(widget: View?) {
                fetchedTextCallback.onClickedIdiomText(meaning)
                //
                AppUtil.makeDebugLog("clicked, the idiom is  "+meaning)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }
        decoratedSpan.setSpan(clickableSpan, index, index + idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return decoratedSpan
    }

    fun getUnderlineTheFetchedWithUntranslatedText(fetchedText: String, decoratedSpan: SpannableString, clickableSpan: ClickableSpan) {
        var untranslatedObserver = object : Consumer<UntranslatedIdiom> {
            override fun accept(t: UntranslatedIdiom?) {
                if (t != null) {
                    var index = BNDMCI().searchString(fetchedText, t?.idiom)
                    if(index != -1){
                        underliningIdiomProcess(index, decoratedSpan, t)
                        AppUtil.makeErrorLog( "nomorr "+index)
                    }
                }
            }

            /* override fun onSubscribe(d: Disposable) {
                     fetchedTextCallback.onFindingUntranslatedIdiom()

             }

             override fun onComplete() {
                 AppUtil.makeDebugLog("im finishedd untranslated")
                 fetchedTextCallback.onFinishedUnderliningText(decoratedSpan)

             }

             override fun onError(e: Throwable) {
                 fetchedTextCallback.onErrorUnderliningText(decoratedSpan)
             }

             override fun onNext(untranslatedIdiom: UntranslatedIdiom) {
                 var index = BNDMCI().searchString(fetchedText, untranslatedIdiom.idiom)
                 underliningIdiomProcess(index, decoratedSpan, untranslatedIdiom, clickableSpan)

                 if(index != -1){
                     AppUtil.makeErrorLog( "nomorr "+index)
                 }ww
             }*/
        }
        DatabaseDataEmitter.untranslatedIdiomList.toObservable().subscribeOn(Schedulers.io()).filter{aduh -> BNDMCI().searchString(fetchedText, aduh.idiom)  != -1 }.observeOn(Schedulers.io()).subscribe(untranslatedObserver)

        //DatabaseDataEmitter.untranslatedIdiomList.toSingle().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(untranslatedObserver)

    }

    //single sentences that contains idiom
    fun getSingleTranslate(idiom : String){
        if(idiom.equals("")){
            AppUtil.makeDebugLog("THE FETCHED DAT CONTAINT IDIOM IS NULL")
            return
        }


        var observer = object : Observer<String> {
            var combineStringMeaning = mutableListOf<String>()
            override fun onSubscribe(d: Disposable) {
                //process startup
                fetchedTextCallback.onTranslatingIdiomOneByOne()
            }

            override fun onComplete() {
                translatedIdiomText
                fetchedTextCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog(e.toString())
                fetchedTextCallback.onErrorTranslatingIdiomOneByOne()
            }

            override fun onNext(t: String) {
                AppUtil.makeDebugLog(t)
                combineStringMeaning.add(t)

            }

        }
        var combineStrings = mutableListOf<String>()

        if(idiom.contains(",")){
            combineStrings.addAll(idiom.split("\\s*,\\s*") )
        }
        else{
            combineStrings.add(idiom)
        }
        translateService.singleTranslate(observer, combineStrings)
    }

    /* fun getTranslatedIdiomOneByOne(){
         if(fetchedPdfText.size == 0){
             AppUtil.makeDebugLog("THE FETCHED DAT CONTAINT IDIOM IS NULL")
             return
         }


         var observer = object : Observer<String> {
             override fun onSubscribe(d: Disposable) {
                 //process startup
                 fetchedTextCallback.onTranslatingIdiomOneByOne()
             }

             override fun onComplete() {
                 translatedIdiomText
                 fetchedTextCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning)
             }

             override fun onError(e: Throwable) {
                 AppUtil.makeErrorLog(e.toString())
                 fetchedTextCallback.onErrorTranslatingIdiomOneByOne()
             }

             override fun onNext(t: String) {
                 AppUtil.makeDebugLog(t)
                 if(t.contains(",")){
                     fetchedIdiomsMeaning.addAll(t.split("\\s*,\\s*") )
                 }

                 else
                     fetchedIdiomsMeaning.add(t)

                 translatedIdiomText = t
             }

         }
         TranslateService().singleTranslate(activity, observer, fetchedPdfText)
     }*/




}