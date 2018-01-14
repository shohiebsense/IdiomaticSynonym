package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import com.shohiebsense.idiomaticsynonym.model.CombinedIdiom
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.UnderliningCallback
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

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
class UnderliningService constructor (val context: Context, val extractedPdfTexts : MutableList<String>) {

    var translatedIdiomText : String = ""
    // lateinit var translatedIdiomList: MutableList<TranslatedIdiom>
    // lateinit var untranslatedIdiomList: MutableList<UntranslatedIdiom>
    lateinit var underliningCallback: UnderliningCallback

    var translatedFetchedPdfText = mutableListOf<String>()
    //var fetchedUntranslatedIdiomTexts = mutableListOf<String>()
    //var fetchedTranslatedIdiomTexts = mutableListOf<String>()
    var translateService : TranslateService
    var indexedSentences = arrayListOf<IndexedSentence>()
    var englishSentences = mutableListOf<String>()
    var indices = arrayListOf<Int>()
    val bookmarkDataEmitter = BookmarkDataEmitter(context)
    lateinit var fileName : String

    init {
        translateService = TranslateService(context)
        extractedPdfTexts.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe {
            englishSentences = AppUtil.splitParagraphsIntoSentences(it)
        }
    }
/*
       JADI GINI, BAWA HASIL IDIOM DALAM BENTUK ARRAYLIST
       HASIL IDIOM DIDAPAT KETIKA MENCOCOKAN TEKS (FETCHEDTEXT) DENGAN IDIOM YANG TERSEDIA (DB)
       POKOKNYA TRANSLASIKAN DULU FETCHED TEXT

       TRANSLASIKAN IDIOM
       BAWA FETCHEDTEXT YANG MERUPAKAN HASIL FETCHED DARI PDF COCOKAN DENGAN TRANSLASI
       TIDAK PERLU TRANSLASI BERSAMAAN, CUKUP YANG DI KLIK AJA.



 */



    constructor(activity: Context, extractedPdfText: MutableList<String>, underliningCallback: UnderliningCallback,fileName : String) : this(activity, extractedPdfText){
        this.underliningCallback = underliningCallback
        this.fileName = fileName
    }


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
    }

    fun init() : UnderliningService {
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
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

    fun translate(){
        val size = 0
        var actualSize = 0

        val sentenceObserver = object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                underliningCallback.onTranslatingText()
            }

            override fun onComplete() {
                if(size == actualSize)
                    underliningCallback.onFinishedTranslatingText()
                AppUtil.makeDebugLog("completed translate with size "+translatedFetchedPdfText.size)

            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error translating pertam ")
                AppUtil.makeErrorLog(e.toString())
                underliningCallback.onErrorTranslatingText()
            }

            override fun onNext(t: String) {
                //AppUtil.makeDebugLog("translated text :\n"+t)
                translatedFetchedPdfText.add(t)
                actualSize++
            }

        }
        // TranslateService().translate(context, observer, fetched)
        //TranslateService().translate(context, observer, "the quick brown fox jumps over the lazy dog. They are dogs and human")

        AppUtil.makeDebugLog("extractedpdftext to translate")
        translateService.translate(sentenceObserver, englishSentences)
    }

    fun getIdiomsObservable() : Observable<ArrayList<CombinedIdiom>>{
        return Observable.create<ArrayList<CombinedIdiom>> {
            e->
            if(!e.isDisposed){
                e.onNext(TranslatedAndUntranslatedDataEmitter.idiomsList)
                e.onComplete()
            }
        }
    }

    fun getTranslatedIdiomListObservable() : Observable<ArrayList<TranslatedIdiom>>? {
        return Observable.create<ArrayList<TranslatedIdiom>>{
            e->
            if(!e.isDisposed){
                e.onNext(TranslatedAndUntranslatedDataEmitter.translatedIdiomList)
                e.onComplete()
            }
        }

    }

    fun getUntranslatedIdiomListObservable() : Observable<ArrayList<UntranslatedIdiom>>? {
        return Observable.create<ArrayList<UntranslatedIdiom>> { e ->
            if (!e.isDisposed) {
                e.onNext(TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList)
                e.onComplete()
            }
        }
    }


    //COBA LANGSUNG DI ONNEXT
    private fun getObserver(): Observer<ArrayList<CharSequence>> {
        return object : Observer<ArrayList<CharSequence>> {
            lateinit var decoratedSpans : ArrayList<CharSequence>
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("SUBSCRIBEEEE MY GAY")
            }


            override fun onNext(sentences : ArrayList<CharSequence>) {
                AppUtil.makeDebugLog("ON NEEEEEEXXTTT ")
                decoratedSpans = sentences
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error underlining :  "+e.toString())

            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed !!!")
                underliningCallback.onFinishedUnderliningText(decoratedSpans)
                bookmarkDataEmitter.insertBookmarkEnglish(fileName, decoratedSpans.toString())
            }
        }
    }

    fun convertToCharSequence(list : ArrayList<CombinedIdiom>): ArrayList<CharSequence> {
        var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())
        var combinedSentences = ""

        englishSentences.forEach {
            combinedSentences += it
        }

        for(combinedIdiom in list){
            englishSentences.forEachIndexed{ sentenceIndex, sentence ->
                if(sentence.contains(combinedIdiom.idiom)){
                    decoratedSpans.add(underliningCombinedIdiomProcess(sentence.indexOf(combinedIdiom.idiom),SpannableStringBuilder(sentence),combinedIdiom,sentenceIndex,sentence))
                    indexedSentences.add(IndexedSentence(sentence, sentenceIndex,fileName))
                    indices.add(sentenceIndex)
                }
            }
        }
        return decoratedSpans
    }

    private fun getObserver2(): Observer<String> {

        var sentenceIndex = 0
        var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

        return object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("SUBSCRIBEEEE MY GAY")
            }


            override fun onNext(sentence : String) {
                TranslatedAndUntranslatedDataEmitter.idiomsList.forEach{index ->
                    AppUtil.makeDebugLog("iterate no. $sentenceIndex, and the idiom is $index")
                    if( sentence.contains(index.idiom)){
                        decoratedSpans.add(underliningCombinedIdiomProcess(sentence.indexOf(index.idiom),SpannableStringBuilder(sentence),index,sentenceIndex,sentence  ))
                    }
                }
                sentenceIndex++
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error underlining :  "+e.toString())

            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed !!! "+decoratedSpans.size)
                underliningCallback.onFinishedUnderliningText(decoratedSpans)
            }
        }
    }



    /*fun findingTranslatedIdiom(translatedIdiomList: List<TranslatedIdiom>, sentence: String, decoratedSpan: SpannableStringBuilder, sentenceIndex: Int) : CharSequence{
        var idiomSpannableBuilder =  decoratedSpan
        for(i in 0 .. translatedIdiomList.size - 1){

            val translatedIdiom = translatedIdiomList[i]
            val index = BNDMCI().searchString(sentence, translatedIdiom.idiom)

            val spaceIndex = BNDMCI().searchString(sentence, " ")
            if(index != -1 && spaceIndex != -1){
                //fetchedPdfText.get(index)
                fetchedTranslatedIdiomTexts.add(translatedIdiom.meaning)
                idiomSpannableBuilder = underliningIdiomProcess(index, decoratedSpan, translatedIdiom,sentenceIndex,sentence)
                indexedSentences.add(IndexedSentence(sentence, sentenceIndex))
            }
        }
        return idiomSpannableBuilder
    }

    fun findingUntranslatedIdiom(untranslatedIdiomList : List<UntranslatedIdiom>, sentence : String, decoratedSpan: SpannableStringBuilder, sentenceIndex: Int) : CharSequence{
        var idiomSpannableBuilder  =  decoratedSpan
        for(i in 0 .. untranslatedIdiomList.size -1){
            val untranslatedIdiom = untranslatedIdiomList[i]
            val index = BNDMCI().searchString(sentence,untranslatedIdiom.idiom)
            val spaceIndex = BNDMCI().searchString(sentence, " ")

            if(index != -1 && spaceIndex != -1){
                AppUtil.makeDebugLog( "nomorr untranslated "+index)

                fetchedUntranslatedIdiomTexts.add(untranslatedIdiom.idiom)
                idiomSpannableBuilder = underliningIdiomProcess(index, decoratedSpan, untranslatedIdiom,sentenceIndex,sentence)
                indexedSentences.add(IndexedSentence(untranslatedIdiom.idiom, sentenceIndex))
            }

        }
        return idiomSpannableBuilder
    }*/

    fun underLine(){
        if(TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty() ){
            AppUtil.makeErrorLog("error size list 0")
            underliningCallback.onErrorUnderliningText()
            return
        }
        //Observable.fromIterable(englishSentences).observeOn(Schedulers.computation()).subscribeOn(Schedulers.computation()).subscribe(getObserver2())

        Observable.create<ArrayList<CombinedIdiom>> { e->
            if(!e.isDisposed){
                e.onNext(TranslatedAndUntranslatedDataEmitter.idiomsList)
                e.onComplete()
            }
        }.observeOn(Schedulers.computation()).subscribeOn(Schedulers.computation())
                .map(object : Function<ArrayList<CombinedIdiom>, ArrayList<CharSequence>>{
                    override fun apply(t: ArrayList<CombinedIdiom>): ArrayList<CharSequence> {
                        return convertToCharSequence(t)
                    }
                }).subscribe(getObserver())

    }

    /*fun getUnderlineZippingV2(){
        if(TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty() ){
            AppUtil.makeErrorLog("error size list 0")
            underliningCallback.onErrorUnderliningText()
            return
        }
        var sentenceIndex = 0
        var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())


        Completable.create{
            subscriber ->
            englishSentences.forEachIndexed{index, sentence ->
                TranslatedAndUntranslatedDataEmitter.idiomsList.forEachIndexed { idiomIndex, idioms ->
                    var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

                    if(sentence.contains(idioms,true)){
                        val index = sentence.indexOf(idioms)
                        val singleSpan = underliningIdiomProcess(index, decoratedSpan, translatedIdiom,sentenceIndex,sentence)


                    }
                    val index = BNDMCI().searchString(value, s)

                    val spaceIndex = BNDMCI().searchString(sentence, " ")
                    if(index != -1 && spaceIndex != -1){
                        //fetchedPdfText.get(index)
                        fetchedTranslatedIdiomTexts.add(translatedIdiom.meaning)
                        indexedSentences.add(IndexedSentence(sentence, sentenceIndex))
                    }
                }
            }
        }




    }
*/

    /*fun getUnderLineZipping(){
        AppUtil.makeDebugLog("extracted pdf size " +extractedPdfTexts.size )
        idiomList = HashMap()
        if(TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.isEmpty() || TranslatedAndUntranslatedDataEmitter.translatedIdiomList.isEmpty() ){
            AppUtil.makeErrorLog("error size list 0")
            underliningCallback.onErrorUnderliningText()
        }


        var sentenceIndex = 0
        var decoratedSpans : ArrayList<CharSequence> = arrayListOf(SpannableStringBuilder())

        Observable.zip(getTranslatedIdiomListObservable(),
                getUntranslatedIdiomListObservable(), BiFunction<ArrayList<TranslatedIdiom>, ArrayList<UntranslatedIdiom>, ArrayList<CharSequence>> {
            translatedIdiomList, untranslatedIdiomList ->

            englishSentences.forEach {
                sentence ->
                AppUtil.makeDebugLog("hiii "+sentenceIndex)
                val sentenceStringBuilder = SpannableStringBuilder("")
                val sentence = AppUtil.newSpaceBetweenSentences(sentence)
                val decoratedSpan =  SpannableStringBuilder(TextUtils.concat(sentenceStringBuilder, findingTranslatedIdiom(translatedIdiomList, sentence, SpannableStringBuilder(sentence) ,sentenceIndex)))
                val decoratedSpan2 = findingUntranslatedIdiom(untranslatedIdiomList, sentence, decoratedSpan, sentenceIndex)
                decoratedSpans.add(decoratedSpan2)
                sentenceIndex++
            }



            *//* englishSentences.forEach { singleString ->
                 val sentence = AppUtil.newSpaceBetweenSentences(singleString)
                 var decoratedSpan =  SpannableStringBuilder(TextUtils.concat(decoratedSpans, findingTranslatedIdiom(translatedIdiomList, sentence, SpannableStringBuilder(sentence) ,sentenceIndex)))
                 decoratedSpans = findingUntranslatedIdiom(untranslatedIdiomList, sentence, decoratedSpan, sentenceIndex)
                 sentenceIndex++
             }*//*

            sentenceIndex++
            decoratedSpans
            //FIND
        }) *//* .filter { it.isNotEmpty() }*//*.subscribeOn(Schedulers.computation()) // Be notified on the main thread
                .observeOn(Schedulers.io())
                .subscribe(getObserver())

    }*/

    //Untranslated
    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableStringBuilder, untranslatedIdiom: UntranslatedIdiom, sentenceIndex: Int, sentence : String) : SpannableStringBuilder {
        return underliningSpannableString(index, decoratedSpan, untranslatedIdiom.idiom, "", sentenceIndex, sentence)
    }

    fun underliningIdiomProcess(index : Int, decoratedSpan: SpannableStringBuilder, translatedIdiom: TranslatedIdiom, sentenceIndex: Int, sentence : String) : SpannableStringBuilder {
        return underliningSpannableString(index,decoratedSpan, translatedIdiom.idiom,translatedIdiom.meaning, sentenceIndex, sentence)

    }

    fun underliningCombinedIdiomProcess(index : Int, decoratedSpan: SpannableStringBuilder, combinedIdiom: CombinedIdiom, sentenceIndex: Int, sentence : String) : SpannableStringBuilder {
        return underliningSpannableString(index,decoratedSpan, combinedIdiom.idiom, combinedIdiom.meaning, sentenceIndex, sentence)

    }

    fun underliningSpannableString(index: Int, decoratedSpan: SpannableStringBuilder, idiom: String, meaning: String, sentenceIndex: Int, sentence: String) : SpannableStringBuilder {
        val endIndex = index + idiom.length
        AppUtil.makeDebugLog("idiom index $index, length $endIndex, and decoratedspan length : ${decoratedSpan.length} also sentence length ${sentence.length}")
        // decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), 0, sentence.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index,endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        AppUtil.makeDebugLog(String.format("underlining %s with index %d", idiom, index))
        //idiomList!!.put(index,meaning)
        val clickableSpan = object : ClickableSpan()
        {
            override fun onClick(widget: View?) {
                AppUtil.makeDebugLog("clicked, the idiom is  "+meaning)

                if(meaning.isBlank()){
                    getSingleTranslate(idiom,sentenceIndex, sentence)
                    return
                }

                underliningCallback.onClickedIdiomText(meaning,sentenceIndex, sentence)
                //
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }
        decoratedSpan.setSpan(clickableSpan, index, index + idiom.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return decoratedSpan
    }


    //single sentences that contains idiom
    fun getSingleTranslate(idiom: String, sentenceIndex: Int, sentence: String){

        val observer = object : Observer<String> {
            var combineStringMeaning = mutableListOf<String>()
            override fun onSubscribe(d: Disposable) {
                //process startup
                underliningCallback.onTranslatingIdiomOneByOne()
            }

            override fun onComplete() {
                translatedIdiomText
                underliningCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning,sentenceIndex)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
                underliningCallback.onErrorTranslatingIdiomOneByOne()
            }

            override fun onNext(t: String) {
                AppUtil.makeDebugLog("singleTranslate "+t+sentence)
                combineStringMeaning.add(t)
                bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence ,idiom) //harusnya bukan idiom, tapi sentence

            }
        }
        val combineStrings = mutableListOf<String>()
        if(idiom.contains(",")){
            combineStrings.addAll(idiom.split("\\s*,\\s*") )
        }
        else{
            combineStrings.add(idiom)
        }
        translateService.singleTranslate(observer, combineStrings)
    }

}