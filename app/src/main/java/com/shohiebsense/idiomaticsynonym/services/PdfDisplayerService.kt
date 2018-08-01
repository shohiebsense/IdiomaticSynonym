package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import com.github.angads25.filepicker.model.DialogConfigs
import com.github.angads25.filepicker.model.DialogProperties
import com.github.angads25.filepicker.view.FilePickerDialog
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.PdfDisplayCallback
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created by Shohiebsense on 05/12/2017.
 */

class PdfDisplayerService(val context: Context) : PDFTextStripper() {
    var pdfValid = false
    lateinit var pdfFile: File
    lateinit var callback: PdfDisplayCallback
    lateinit var loadedDocument : PDDocument


    companion object {
        val STATUS_LOADING = 5
        val ERROR_LOAD = 0
        val ERROR_FETCH = 1
        val ERROR_TRANSLATE = 2
        val STATUS_LOADED = 10
        val STATUS_RESUMED = 11
        val STATUS_FETCHED = 9
        val STATUS_TRANSLATED = 8
        val STATUS_FETCHED_DB = 7
        val STATUS_COMPLETED = 6
        val STATUS_INIT = 3
    }
    

    constructor(activity: Context, callback: PdfDisplayCallback) : this(activity) {
        this.callback = callback
    }
    fun init(): PdfDisplayerService {
        PDFBoxResourceLoader.init(context)
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }




    fun promptLoadPdfDialog() {
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
            files.forEach { fileString ->
                pdfValid = AppUtil.isPdfDocument(fileString)
                if (pdfValid) {
                    loadPdf(fileString)
                }
                else{
                   callback.onErrorReadingFile()
                }
            }
        }
        dialog.show()
    }

    fun loadPdf(fileString: String) {
        pdfFile = File(fileString)
        loadedDocument = PDDocument.load(pdfFile)
        callback.onFinishedLoadingPdf()
    }

    fun fetchText(from : Int,to : Int){
        callback.onLoadingPdf()
        val myObserver = object : Observer<String> {
            var fetchedPdfText = mutableListOf<String>()
            override fun onComplete() {
                AppUtil.makeDebugLog("completed")
            }
            override fun onError(e: Throwable) {
                callback.onError()
            }
            override fun onSubscribe(@NonNull d: Disposable) {
                callback.onProcess()
            }
            override fun onNext(text: String) {
                fetchedPdfText.add(text)
                callback.onEmitted(text,pdfFile.name)
            }
        }
        getTextFromPdf(myObserver,from,to)
    }


    fun getTextFromPdf(myObserver: Observer<String>, from: Int, to : Int) {
        Observable.create<String> { subscriber ->
            var pdfStripper = PDFTextStripper()
            AppUtil.makeErrorLog("jalanssz "+loadedDocument.numberOfPages)
            pdfStripper.startPage = from
            pdfStripper.endPage = to
            var parsedText = pdfStripper.getText(loadedDocument)
            subscriber.onNext(parsedText)
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