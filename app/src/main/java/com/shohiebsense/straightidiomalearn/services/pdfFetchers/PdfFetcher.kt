package com.shohiebsense.straightidiomalearn.services.pdfFetchers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.eaio.stringsearch.BNDMCI
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.pdf.PdfFileModel
import com.shohiebsense.straightidiomalearn.services.TranslateService
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchCallback
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.io.*

import java.util.ArrayList

/**
 * Created by Shohiebsense on 09/09/2017.
 *
 * punya kemampuan OCR -> read image to txt
 */

/*
class PdfFetcher(val context : Context, val uri : Uri) {
*/
class PdfFetcher constructor (val activity : Context) {

    lateinit var inputStream : InputStream
    var pdfValid = false
    lateinit var file : File
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

    fun init() : PdfFetcher {
        PDFBoxResourceLoader.init(activity)
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }

    fun promptLoadPdf(){
        callback.onLoadingPdf()
        var properties =  DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root =  File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir =  File(DialogConfigs.DEFAULT_DIR);
        properties.offset = File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        var dialog = FilePickerDialog(activity,properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("pilih")
        dialog.setDialogSelectionListener {
            //handle selected files
            files ->

            AppUtil.makeDebugLog("filename size "+ files.size)

            files.forEach {
                fileString ->
                pdfValid = AppUtil.isPdfDocument(fileString)

                if(pdfValid){
                    loadPdf(fileString)
                }



            }
        }
        dialog.show()
    }

    fun loadPdf(fileString : String){
        file = File(fileString)

        // copy(file)

        var pdfFileModel = PdfFileModel(file.name,file.absolutePath)
        AppUtil.makeDebugLog("filename "+ file.name)
        AppUtil.makeDebugLog("filepath "+ file.absolutePath)


        callback.onFinishedLoadingPdf(file)

        //file = FilePickerUriHelper.getFile(context, data);
    }



    fun getTextFromPdf(myObserver: Observer<String>, destinationFile: File) {

        Observable.create<String> { subscriber ->
            AppUtil.makeDebugLog("jalann "+destinationFile.absolutePath)
            var document = PDDocument.load(destinationFile)

            //document = PDDocument.load(context.assets.open("samplepdf.pdf"))
            //document =
            var fetchedText = mutableListOf<String>()
            var pdfStripper = PDFTextStripper()

            for(i in 1 .. document.numberOfPages ){
                pdfStripper.startPage = i
                pdfStripper.endPage = i
                var parsedText = "Parsed text: \n\n" + pdfStripper.getText(document)
                subscriber.onNext(parsedText)
                AppUtil.makeDebugLog(" page : .." + i + parsedText)
            }
            document.close()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver)


    }

    fun copy(file : File) {
        try {
            val directory = File(rootDirectory)
            if(!directory.exists()){
                directory.mkdir();
            }


            destinationFile = File(directory, "namee.pdf")
            var inputStream = FileInputStream(file)
            val os = FileOutputStream(destinationFile)
            var byteRead = 0
            val buf = ByteArray(1024)
            while ( inputStream.read(buf).let { byteRead = inputStream.read(buf) ; byteRead != -1}) {
                os.write(buf, 0, byteRead)
            }

            /*  do{
                  byteRead = inputStream.read(buf)
                  os.write(buf, 0, byteRead)
              }while (inputStream.read(buf) > 0)*/

            inputStream.close()
            os.flush()
            os.close()
            AppUtil.makeDebugLog("sizzee "+destinationFile.length()+ "  "+destinationFile.absolutePath)

        } catch (e: IOException) {
            AppUtil.makeErrorLog("io error " +e.toString())
        }

    }

    fun fetchText(){

        val myObserver = object : Observer<String> {

            override fun onComplete() {
                AppUtil.makeDebugLog("beresss" +
                        "")

                //ccmmented due to fetched text test, uncommented and change to mutablelist
                callback.onFinishedFetchingPdf(fetchedPdfText)
            }


            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("rusakk "+e.toString())
            }

            override fun onSubscribe(@NonNull d: Disposable) {
                callback.onFetchingPdf()
            }

            override fun onNext(text: String) {
                AppUtil.makeDebugLog("jadi threadd $text")
               // fetchedText = text
                fetchedPdfText.add(text)
            }
        }

        getTextFromPdf(myObserver, file)
    }


    fun threading(){



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
        var observer = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                fetchedTextCallback.onTranslatingText()
            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed translate")
                fetchedTextCallback.onFinishedTranslatingText()
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error translating pertama")
                AppUtil.makeErrorLog(e.toString())
                fetchedTextCallback.onErrorTranslatingText()
            }

            override fun onNext(t: String) {
                AppUtil.makeDebugLog(t)
                translatedText = t
            }

        }
       // TranslateService().init(activity, observer, fetched)
        TranslateService().init(activity, observer, "the quick brown fox jumps over the lazy dog. They are dogs and human")


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
        //TranslateService().init(activity, observer, fetched)
    }

    fun getUnderlineTheFetchedText(fetchedText : ArrayList<String>){

        //change to arraylist ...

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
                    var clickableSpan = object : ClickableSpan(){

                        override fun onClick(p0: View?) {
                            AppUtil.makeDebugLog("kok ga keluarr")
                            Toast.makeText(activity, "anuu "+(p0 as TextView).text, Toast.LENGTH_LONG).show()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            ds.isUnderlineText =false
                        }
                    }

                    val translatedObserver = object : Observer<TranslatedIdiom> {

                        override fun onComplete() {
                            AppUtil.makeDebugLog("finished translation db")
                            fetchedTextCallback.onFinishedFindingTranslatedIdiom(anusingle, decoratedSpan, clickableSpan)
                        }

                        override fun onError(e: Throwable) {
                            fetchedTextCallback.onErrorUnderliningText(decoratedSpan)
                        }

                        override fun onSubscribe(@NonNull d: Disposable) {
                            fetchedTextCallback.onFindingTranslatedIdiom()
                        }

                        override fun onNext(translatedIdiom: TranslatedIdiom) {
                            var index = BNDMCI().searchString(anusingle, translatedIdiom.idiom)
                            if(index != -1){
                                AppUtil.makeErrorLog( "nomorr "+index)
                                underliningIdiomProcess(index, decoratedSpan, translatedIdiom, clickableSpan)
                            }
                        }
                    }

                    if(DatabaseDataEmitter.untranslatedIdiomList.isEmpty() || DatabaseDataEmitter.translatedIdiomList.isEmpty() ){
                        AppUtil.makeErrorLog("error size list 0")
                        fetchedTextCallback.onErrorUnderliningText(decoratedSpan);
                    }
                    if(translatedText.isBlank() || translatedText.isEmpty()){
                        AppUtil.makeErrorLog("translate-an kosong")
                        fetchedTextCallback.onErrorUnderliningText(decoratedSpan);
                    }


                    DatabaseDataEmitter.translatedIdiomList.toObservable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(translatedObserver)
                }
        //var anuu = AppUtil.newSpaceInString(fetchedText)






     /*   var consumer = object : Consumer<UntranslatedIdiom> {
            override fun accept(t: UntranslatedIdiom) {
                var index = BNDMCI().searchString(anuu,t.idiom)
                if(index != -1){
                    AppUtil.makeErrorLog( "nomorr "+index)

                    decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), 0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index,index+t.idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    decoratedSpan.setSpan(object: ClickableSpan(){
                        override fun onClick(p0: View?) {
                            AppUtil.makeDebugLog("kok ga keluarr")
                            Toast.makeText(context, "anuu "+t.idiom, Toast.LENGTH_LONG).show()
                        }

                        override fun getUnderlying(): CharacterStyle {
                            return super.getUnderlying()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    }, index, index+t.idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    decoratedSpan.setSpan(clickableSpan, index, index + t.idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
            }

        }*/
        //
    }


    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableString, untranslatedIdiom: UntranslatedIdiom, clickableSpan: ClickableSpan){
        underliningSpannableString(index,decoratedSpan, untranslatedIdiom.idiom, clickableSpan, "")
    }

    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableString, translatedIdiom: TranslatedIdiom, clickableSpan: ClickableSpan){
        underliningSpannableString(index,decoratedSpan, translatedIdiom.idiom, clickableSpan, translatedIdiom.meaning)

    }

    fun underliningSpannableString(index : Int, decoratedSpan : SpannableString, idiom : String, clickableSpan : ClickableSpan, meaning : String) : SpannableString{
        decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(activity, R.color.secondaryDarkColor)), 0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index,index+idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        decoratedSpan.setSpan(object: ClickableSpan(){
            override fun onClick(p0: View?) {
               /* if(meaning.isEmpty()){
                    translateIdiom(idiom)
                }
                else{*/
                    fetchedTextCallback.onClickedIdiomText(meaning)
               // }
                AppUtil.makeDebugLog("kok ga keluarr")
                Toast.makeText(activity, "anuu "+idiom, Toast.LENGTH_LONG).show()
            }

            override fun getUnderlying(): CharacterStyle {
                return super.getUnderlying()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }, index, index+idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        decoratedSpan.setSpan(clickableSpan, index, index + idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return decoratedSpan
    }

    fun getUnderlineTheFetchedWithUntranslatedText(fetchedText: String, decoratedSpan: SpannableString, clickableSpan: ClickableSpan) {
        var untranslatedObserver = object : Observer<UntranslatedIdiom> {
            override fun onComplete() {
                AppUtil.makeDebugLog("im finishedd untranslated")
                fetchedTextCallback.onFinishedUnderliningText(decoratedSpan)

            }

            override fun onError(e: Throwable) {
                fetchedTextCallback.onErrorUnderliningText(decoratedSpan)
            }

            override fun onSubscribe(@NonNull d: Disposable) {
                fetchedTextCallback.onFindingUntranslatedIdiom()

            }

            override fun onNext(untranslatedIdiom: UntranslatedIdiom) {
                var index = BNDMCI().searchString(fetchedText, untranslatedIdiom.idiom)
                if(index != -1){
                    AppUtil.makeErrorLog( "nomorr "+index)
                    underliningIdiomProcess(index, decoratedSpan, untranslatedIdiom, clickableSpan)

                }
            }
        }
        DatabaseDataEmitter.untranslatedIdiomList.toObservable().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(untranslatedObserver)
    }
}