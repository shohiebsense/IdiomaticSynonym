package com.shohiebsense.idiomaticsynonym.obsoletes.underliningservice

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
import com.shohiebsense.idiomaticsynonym.services.TranslateService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.UnderliningCallback
import com.tom_roush.pdfbox.pdmodel.PDResources
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.regex.Pattern
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
    var translatedFetchedPdfText = arrayListOf<SpannableStringBuilder>()
    //var fetchedUntranslatedIdiomTexts = mutableListOf<String>()
    //var fetchedTranslatedIdiomTexts = mutableListOf<String>()
    lateinit var translateService : TranslateService
    var indexedSentences = arrayListOf<Link>()
    var indices = arrayListOf<Int>()
    val bookmarkDataEmitter = BookmarkDataEmitter(context)
    lateinit var fileName : String
    lateinit var  extractedPdfTexts : CharSequence
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



    constructor(activity: Context, extractedPdfText: CharSequence, underliningCallback: UnderliningCallback,fileName : String) : this(activity){
        this.extractedPdfTexts = extractedPdfText
        this.underliningCallback = underliningCallback
        this.fileName = fileName
    }

    constructor(activity: Context, extractedPdfText:String, underliningCallback: UnderliningCallback,fileName : String) : this(activity){
        this.extractedPdfTexts = extractedPdfText
        this.underliningCallback = underliningCallback
        this.fileName = fileName
        //englishSentences = AppUtil.splitParagraphsIntoSentences(extractedPdfString)
    }

    fun init() : UnderliningService {
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }

    private fun getObserver(): Observer<ArrayList<Link>> {
        return object : Observer<ArrayList<Link>> {
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("Underlining Begins ")
            }


            override fun onNext(sentences : ArrayList<Link>) {
                indexedSentences = sentences
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error underlining :  "+e.toString())

            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed !!!")
                underliningCallback.onFinishedUnderliningText(indexedSentences)
                //translate()
            }
        }
    }

    fun getSentence(text: String, word: String): String? {
        val END_OF_SENTENCE = Pattern.compile("\\s+[^.!?]*[.!?]")
        val lcword = word.toLowerCase()
        for (sentence in END_OF_SENTENCE.split(text)) {
            if (sentence.toLowerCase().contains(lcword)) {
                return sentence
            }
        }
        return null
    }

    fun convertToCharSequence(list : ArrayList<CombinedIdiom>): ArrayList<Link> {

        translateService = TranslateService(context)
        val spannableStringBuilder = SpannableStringBuilder(extractedPdfTexts)
        AppUtil.makeDebugLog("Extractedpdfstring "+extractedPdfTexts)
        var flagged = false
        val END_OF_SENTENCE = Pattern.compile("\\s+[^.!?]*[.!?]")

        for(combinedIdiom in list) {
            val matcher = Pattern.compile("[^.]*" + Pattern.quote(combinedIdiom.idiom) + "[^.]*\\.?", Pattern.CASE_INSENSITIVE).matcher(extractedPdfTexts)
            while (matcher.find()) {
                AppUtil.makeDebugLog("matcher findd " + matcher.group().trim())
                spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                spannableStringBuilder.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), matcher.start(), matcher.end(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View?) {
                        if (combinedIdiom.meaning.isBlank()) {
                            getSingleTranslate(combinedIdiom.idiom)
                            return
                        }
                        underliningCallback.onClickedIdiomText(combinedIdiom.meaning)
                        //
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                spannableStringBuilder.setSpan(clickableSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                flagged = true
            }
          //  indexedSentences.add(TempIndexedSentence(spannableStringBuilder,flagged))
        }



       /* MaxentTagger.tokenizeText(StringReader(extractedPdfString)).forEachIndexed { sentenceIndex, sentenceChar ->
            var sentence = Sentence.listToString(sentenceChar)
            var spannableStringBuilder = SpannableStringBuilder(sentence)
            var flagged = false
            for(combinedIdiom in list){


                var index = sentence.toLowerCase().indexOf(combinedIdiom.idiom+"\\b",0,true)

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
                                getIdiomTranslate(combinedIdiom.idiom,sentenceIndex, sentence)
                                return
                            }
                            underliningCallback.onClickedIdiomText(combinedIdiom.meaning)
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
        }*/
        return indexedSentences
    }

    fun extractTranslation(indexedSentence: Link){
        translatedFetchedPdfText.add(translateService.translate(indexedSentence,true)!!)
    }

    fun getError(e: String){
        AppUtil.makeErrorLog("error translating pertam ")
        AppUtil.makeErrorLog(e.toString())
        underliningCallback.onErrorTranslatingText(e)
    }

    fun translateCompletion(){
        AppUtil.makeDebugLog("COMPLETED ALL TRANSLATION")
        underliningCallback.onFinishedTranslatingText()
        var spannableStringBuilder = SpannableStringBuilder()
        translatedFetchedPdfText.forEach {
            spannableStringBuilder.append(it)
        }
        //bookmarkDataEmitter.updateTranslation(spannableStringBuilder, sentenceIndex)
    }

    fun translate(){
        indexedSentences.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribeBy  (
             onNext = { extractTranslation(it) },
                    onError =  { getError(it.toString()) },
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
                .map(object : Function<ArrayList<CombinedIdiom>, ArrayList<Link>>{
                    override fun apply(t: ArrayList<CombinedIdiom>): ArrayList<Link> {
                        return convertToCharSequence(t)
                    }
                }).subscribe(getObserver())

    }

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
                    //getIdiomTranslate(idiom,sentenceIndex, sentence)
                    return
                }

                underliningCallback.onClickedIdiomText(meaning)
                //
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }
        //bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence ,idiom) //harusnya bukan idiom, tapi sentence
        decoratedSpan.setSpan(clickableSpan, index, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return decoratedSpan
    }


    fun getSingleTranslate(idiom: String){
        val observer = object : Observer<String> {
            var combineStringMeaning = mutableListOf<String>()
            override fun onSubscribe(d: Disposable) {
                //process startup
                underliningCallback.onTranslatingIdiomOneByOne()
            }

            override fun onComplete() {
                translatedIdiomText
                underliningCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning)
            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("e singleTranslating "+ e.toString())
                underliningCallback.onErrorTranslatingIdiomOneByOne()
            }

            override fun onNext(t: String) {
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
        translateService.singleTranslate(observer, combineStrings)
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
                underliningCallback.onFinishedTranslatingIdiomOneByOne(combineStringMeaning)
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