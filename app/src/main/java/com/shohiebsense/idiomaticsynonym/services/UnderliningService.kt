package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.*
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.UnderliningCallback
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import edu.stanford.nlp.ling.Sentence
import edu.stanford.nlp.tagger.maxent.MaxentTagger
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.io.StringReader
import kotlin.collections.ArrayList

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
class UnderliningService constructor (val context: Context) {

    var translatedIdiomText : String = ""
    // lateinit var translatedIdiomList: MutableList<TranslatedIdiom>
    // lateinit var untranslatedIdiomList: MutableList<UntranslatedIdiom>
    lateinit var underliningCallback: UnderliningCallback
    var extractedPdfString = String()
    var translatedFetchedPdfText = arrayListOf<SpannableStringBuilder>()
    //var fetchedUntranslatedIdiomTexts = mutableListOf<String>()
    //var fetchedTranslatedIdiomTexts = mutableListOf<String>()
    private var translateService : TranslateService = TranslateService(context)
    var indexedSentences = arrayListOf<TempIndexedSentence>()
    var indices = arrayListOf<Int>()
    val bookmarkDataEmitter = BookmarkDataEmitter(context)
    lateinit var fileName : String
    var  extractedPdfTexts = arrayListOf<String>()
    init {
        /* extractedPdfTexts.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribe {
            englishSentences = AppUtil.splitParagraphsIntoSentences(it)
        }*/
    }
/*
       JADI GINI, BAWA HASIL IDIOM DALAM BENTUK ARRAYLIST
       HASIL IDIOM DIDAPAT KETIKA MENCOCOKAN TEKS (FETCHEDTEXT) DENGAN IDIOM YANG TERSEDIA (DB)
       POKOKNYA TRANSLASIKAN DULU FETCHED TEXT

       TRANSLASIKAN IDIOM
       BAWA FETCHEDTEXT YANG MERUPAKAN HASIL FETCHED DARI PDF COCOKAN DENGAN TRANSLASI
       TIDAK PERLU TRANSLASI BERSAMAAN, CUKUP YANG DI KLIK AJA.



 */



    constructor(activity: Context, extractedPdfText: ArrayList<String>, underliningCallback: UnderliningCallback,fileName : String) : this(activity){
        this.extractedPdfTexts = extractedPdfText
        this.underliningCallback = underliningCallback
        this.fileName = fileName
    }

    constructor(activity: Context, extractedPdfText:String, underliningCallback: UnderliningCallback,fileName : String) : this(activity){
        this.extractedPdfString = extractedPdfText
        this.underliningCallback = underliningCallback
        this.fileName = fileName
        //englishSentences = AppUtil.splitParagraphsIntoSentences(extractedPdfString)
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

    private fun getObserver(): Observer<ArrayList<TempIndexedSentence>> {
        return object : Observer<ArrayList<TempIndexedSentence>> {
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("SUBSCRIBEEEE MY GAY")
            }


            override fun onNext(sentences : ArrayList<TempIndexedSentence>) {
                indexedSentences = sentences
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error underlining :  "+e.toString())

            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed !!!")
                underliningCallback.onFinishedUnderliningText(indexedSentences)
                translate()
            }
        }
    }

    fun convertToCharSequence(list : ArrayList<CombinedIdiom>): ArrayList<TempIndexedSentence> {
        val idiomSet = HashSet<String>()
        val links = arrayListOf<Link>()
        /*   val simpleText = SimpleText.create(context, extractedPdfString)
           for(combinedIdiom in list) {
               var index = extractedPdfString.indexOf(combinedIdiom.idiom)
               if (index >= 0 && idiomSet.add(combinedIdiom.idiom) && extractedPdfString.get(combinedIdiom.idiom.length + 1).equals("\\s")) {
                   simpleText.textColor(R.color.other_red)

               }
           }*/
        MaxentTagger.tokenizeText(StringReader(extractedPdfString)).forEachIndexed { sentenceIndex, sentenceChar ->
            var sentence = Sentence.listToString(sentenceChar)
            var spannableStringBuilder = SpannableStringBuilder(sentence)
            var flagged = false
            for(combinedIdiom in list){
                var index = sentence.indexOf(combinedIdiom.idiom+"\\b",0,true)

                //  val matcher =Pattern.compile( combinedIdiom.idiom+"\\b",Pattern.CASE_INSENSITIVE).matcher(sentence)
                // if(matcher.find()){
                if(index >= 0){
                    // AppUtil.makeDebugLog("ketemu lhooo "+matcher.start()+"  " + matcher.end() + combinedIdiom + "  -  "+sentence)
                    //spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    //idiomList!!.put(index,meaning)
                    var lastIndex = index+combinedIdiom.idiom.length-1
                    spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), index, index+combinedIdiom.idiom.length-1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    spannableStringBuilder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), index, lastIndex, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View?) {
                            if (combinedIdiom.meaning.isBlank()) {
                                getSingleTranslate(combinedIdiom.idiom,sentenceIndex, sentence)
                                return
                            }
                            underliningCallback.onClickedIdiomText(combinedIdiom.meaning, sentenceIndex, sentence)
                            //
                        }
                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                        }
                    }
                    spannableStringBuilder.setSpan(clickableSpan, index, lastIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                    //bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence, combinedIdiom.idiom)
                    flagged = true
                    //indices.add(sentenceIndex)
                    //underliningCombinedIdiomProcess(index,spannableStringBuilder,combinedIdiom,sentenceIndex,sentence)
                    //indices.add(sentenceIndex)
                }
            }
            indexedSentences.add(TempIndexedSentence(spannableStringBuilder,flagged))
        }
        return indexedSentences
    }

    fun extractTranslation(indexedSentence: TempIndexedSentence){
        var spannableStringBuilder = SpannableStringBuilder(indexedSentence.sentence)
        if(indexedSentence.flagged){
            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD),0,indexedSentence.sentence.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        translatedFetchedPdfText.add(spannableStringBuilder)
    }

    fun getError(e: Unit){
        AppUtil.makeErrorLog("error translating pertam ")
        AppUtil.makeErrorLog(e.toString())
        underliningCallback.onErrorTranslatingText()
    }

    fun translateCompletion(){
        AppUtil.makeDebugLog("COMPLETED ALL TRANSLATION")
        underliningCallback.onFinishedTranslatingText()
        var spannableStringBuilder = SpannableStringBuilder()
        translatedFetchedPdfText.forEach {
            spannableStringBuilder.append(it)
        }
        bookmarkDataEmitter.updateIndonesianText(spannableStringBuilder)

    }

    fun translate(){
        indexedSentences.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribeBy  (
             onNext = { extractTranslation(it) },
                    onError =  { getError(it.printStackTrace()) },
                    onComplete = { translateCompletion() }
        )
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



/*private fun getObserver2(): Observer<String> {

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
}*/



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
        }.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.computation())
                .map(object : Function<ArrayList<CombinedIdiom>, ArrayList<TempIndexedSentence>>{
                    override fun apply(t: ArrayList<CombinedIdiom>): ArrayList<TempIndexedSentence> {
                        bookmarkDataEmitter.insertBookmarkEnglish(fileName, "","")
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
        var endIndex = index + idiom.length
        decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),index,endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        AppUtil.makeDebugLog(String.format("underlining %s with index %d", idiom, index))
        //idiomList!!.put(index,meaning)
        val clickableSpan = object : ClickableSpan()
        {
            override fun onClick(widget: View?) {
                AppUtil.makeDebugLog("clicked, the idiom is  "+meaning)

                if(meaning.isBlank()){
                    //getSingleTranslate(idiom,sentenceIndex, sentence)
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
        bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence ,idiom) //harusnya bukan idiom, tapi sentence
        decoratedSpan.setSpan(clickableSpan, index, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return decoratedSpan
    }


    fun getSingleTranslate(idiom: String){

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

            }
        }
        val combineStrings = mutableListOf<String>()
        if(idiom.contains(",")){
            combineStrings.addAll(idiom.split("\\s*,\\s*") )
        }
        else{
            combineStrings.add(idiom)
        }
        //translateService.singleTranslate(observer, combineStrings)
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
}