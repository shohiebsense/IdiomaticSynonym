package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.PdfDisplayCallback
import com.tom_roush.pdfbox.pdfparser.PDFParser
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.OutputStreamWriter

/**
 * Created by Shohiebsense on 05/12/2017.
 */

class PdfDisplayerService(val context: Context) : PDFTextStripper() {
    var pdfValid = false
    lateinit var pdfFile: File
    lateinit var callback: PdfDisplayCallback

    constructor(activity: Context, callback: PdfDisplayCallback) : this(activity) {
        this.callback = callback
    }
    fun init(): PdfDisplayerService {
        PDFBoxResourceLoader.init(context)
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }


    fun promptLoadPdf() {
        var properties = DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = File(DialogConfigs.DEFAULT_DIR);
        properties.offset = File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;


        var dialog = FilePickerDialog(context, properties);
        dialog.setTitle("Select a File");
        dialog.setPositiveBtnName("pilih")

        dialog.setDialogSelectionListener {
            //handle selected files
            files ->

            AppUtil.makeDebugLog("filename size " + files.size)

            files.forEach { fileString ->
                pdfValid = AppUtil.isPdfDocument(fileString)

                if (pdfValid) {
                    loadPdf(fileString)
                }
            }
        }
        dialog.show()
    }

    fun loadPdf(fileString: String) {

        // copy(file)
        callback.onFinishedLoadingPdf(fileString)

        //file = FilePickerUriHelper.getFile(context, data);
    }

    fun fetchText(numberOfPages : Int){
        callback.onLoadingPdf()
        val myObserver = object : Observer<String> {
            var fetchedPdfText = mutableListOf<String>()

            override fun onComplete() {
                AppUtil.makeDebugLog("beresss fetched pdf text, size = " + fetchedPdfText.size)
                //ccmmented due to fetched text test, uncommented and change to mutablelist
               // callback.onFinishedFetchingPdfAsList(fetchedPdfText,pdfFile.name)
            }


            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("rusakk "+e.toString())
            }

            override fun onSubscribe(@NonNull d: Disposable) {
                callback.onFetchingPdf()
            }

            override fun onNext(text: String) {
                // extractedPdfTexts = text
                fetchedPdfText.add(text)
                callback.onFinishedFetchingPdf(text,pdfFile.name)

            }
        }

        getTextFromPdf(myObserver, pdfFile, numberOfPages)
    }


    fun getTextFromPdf(myObserver: Observer<String>, destinationFile: File, numberOfPages: Int) {

        Observable.create<String> { subscriber ->
            AppUtil.makeDebugLog("jalann "+destinationFile.absolutePath)
            var document = PDDocument.load(destinationFile)

            var pdfStripper = PDFTextStripper()

            //FOR DEVELOPMENT ONLY
            var numberOfPages2 = 2

            //document = PDDocument.load(context.assets.open("samplepdf.pdf"))
            //document =
            pdfStripper.lineSeparator = "\n"

            // var endPage = if(numberOfPages > document.numberOfPages) document.numberOfPages else numberOfPages

            //FOR DEVELOPMENT , UNCOMMENT ABOVE
            var endPage = if(numberOfPages2 > document.numberOfPages) document.numberOfPages else numberOfPages2


            //fir development only, change to 1
            //for(i in 2 .. endPage ){
            pdfStripper.startPage = 1
            pdfStripper.endPage = 2
                //AppUtil.makeDebugLog(" page : .." + i + parsedText)
           // }

            var parsedText = pdfStripper.getText(document)
            subscriber.onNext(parsedText)

            //second way
           /* val parser = PDFParser(destinationFile)
            parser.parse()
            val cosdoc = parser.document
            val pdfStripper2= PDFTextStripper()
            val pddoc = PDDocument(cosdoc)
            pdfStripper2.startPage = 1
            pdfStripper2.endPage =2
            var text = pdfStripper2.getText(pddoc)
            subscriber.onNext(text)*/
            document.close()
            subscriber.onComplete()
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(myObserver)


    }


    override fun writeString(text: String?) {
        super.writeString(text)

    }
}