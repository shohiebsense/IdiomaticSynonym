package com.shohiebsense.idiomaticsynonym.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.support.design.widget.BottomSheetBehavior
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import com.klinker.android.link_builder.Link
import com.shohiebsense.idiomaticsynonym.model.*
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
import org.apache.commons.lang3.time.StopWatch
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
class UnderliningServiceUsingContains constructor (val context: Context) {

    var translatedIdiomText : String = ""
    lateinit var underliningCallback: UnderliningCallback
    var translatedFetchedPdfText = arrayListOf<SpannableStringBuilder>()
    lateinit var translateService : TranslateService
    var clickableIdioms = arrayListOf<Link>()
    var sentences = mutableListOf<String>()
    val bookmarkDataEmitter = BookmarkDataEmitter(context)
    lateinit var fileName : String
    lateinit var  extractedPdfTexts : CharSequence
    var sentenceIndex : StringBuilder = StringBuilder("")

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
        sentences = AppUtil.splitParagraphsIntoSentences(extractedPdfTexts.toString())
    }

    constructor(activity: Context, extractedPdfText:String, underliningCallback: UnderliningCallback,fileName : String) : this(activity){
        this.extractedPdfTexts = extractedPdfText
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
        val STATUS_RESUMED = 11
        val STATUS_FETCHED = 9
        val STATUS_TRANSLATED = 8
        val STATUS_FETCHED_DB = 7
        val STATUS_COMPLETED = 6
        val STATUS_INIT = 3
    }

    fun init() : UnderliningServiceUsingContains {
        //inputStream = context.contentResolver.openInputStream(uri)
        return this
    }

    private fun getLinksObserver(): Observer<ArrayList<Link>> {
        return object : Observer<ArrayList<Link>> {
            override fun onSubscribe(d: Disposable) {
                AppUtil.makeDebugLog("Underlining Begins ")
            }


            override fun onNext(cliclableIdioms : ArrayList<Link>) {
                clickableIdioms = cliclableIdioms
            }

            override fun onError(e: Throwable) {
                AppUtil.makeDebugLog("error underlining :  "+e.toString())

            }

            override fun onComplete() {
                AppUtil.makeDebugLog("completed !!!")
                underliningCallback.onFinishedUnderliningText(clickableIdioms)
                translate()
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

    fun convertToCharSequence(combinedIdioms: ArrayList<CombinedIdiom>, behaviour: BottomSheetBehavior<View>): ArrayList<Link> {

        if(AppUtil.checkInternetConnection(context)){
            translateService = TranslateService(context)
        }
        val singleCombinedIdiom = HashSet<String>()
        val timer = StopWatch()
        timer.start()
        //MaxentTagger.tokenizeText(StringReader(extractedPdfTexts.toString())).forEachIndexed { sentenceIndex, sentenceChar ->
        AppUtil.makeDebugLog("tokenize finished")
        // var sentence = Sentence.listToString(sentenceChar)
        // var spannableStringBuilder = SpannableStringBuilder(sentence)
        val idioms = StringBuilder()
        for(i in combinedIdioms.indices){
            if(extractedPdfTexts.contains(combinedIdioms[i].idiom) && singleCombinedIdiom.add(combinedIdioms[i].idiom)) {
                var index = extractedPdfTexts.toString().indexOf(combinedIdioms[i].idiom,0,true)
                val prevIndex = extractedPdfTexts[index-1].toLowerCase()
                val afterLastIndex = extractedPdfTexts[index+combinedIdioms[i].idiom.length].toLowerCase()
                var bool = prevIndex.isLetterOrDigit()
                var boolFinal = afterLastIndex.isLetter()
                if(index >= 0 && (!bool && !boolFinal)){
                    idioms.append(combinedIdioms[i].idiom+", ")
                    //AppUtil.makeErrorLog("this is worddd "+ combinedIdioms[i].idiom + " last char "+ afterLastIndex+ "  prevChar "+ prevIndex)
                    //AppUtil.makeErrorLog("this is real word "+ combinedIdioms[i].idiom + " last char "+ extractedPdfTexts[index] + "  prevChar "+ extractedPdfTexts[index+combinedIdiom.idiom.length])
                    sentenceIndex.append("$i, ")

                    val link = Link(Pattern.compile("[\\s]"+ combinedIdioms[i].idiom+"[^a-z]",Pattern.CASE_INSENSITIVE))
                            .setTextColor(Color.parseColor("#00BCD4"))
                            .setUnderlined(false)
                            .setOnClickListener {  if (combinedIdioms[i].meaning.isBlank()) {
                                behaviour.state = BottomSheetBehavior.STATE_HIDDEN
                                getSingleTranslate(combinedIdioms[i].idiom,index, extractedPdfTexts.toString())
                            }
                            else{
                                underliningCallback.onClickedIdiomText(combinedIdioms[i].meaning)
                            }}

                    //record to the database
                    clickableIdioms.add(link)

                }
            }
        }

        bookmarkDataEmitter.updateIdioms(idioms.toString())
        timer.stop()
        val seconds = timer.time/60
        Log.e("shohiebsense ","time ellapsedd seconds "+seconds)
        Log.e("shohiebsenseeee ","linkss size "+clickableIdioms.size)
        return clickableIdioms
    }

    fun extractTranslation(indexedSentence: String){
        translatedFetchedPdfText.add(translateService.translate(indexedSentence,true)!!)
    }

    fun getError(e: Unit){
        AppUtil.makeErrorLog("error translating pertam ")
        AppUtil.makeErrorLog(e.toString())
        underliningCallback.onErrorTranslatingText()
    }

    fun translateCompletion(){
        AppUtil.makeDebugLog("COMPLETED ALL TRANSLATION")
        var spannableStringBuilder = SpannableStringBuilder()
        translatedFetchedPdfText.forEach {
            spannableStringBuilder.append(it)
        }

        AppUtil.makeErrorLog("finished the indonesian  "+spannableStringBuilder.toString())
        bookmarkDataEmitter.updateIndonesianText(spannableStringBuilder,sentenceIndex)
        underliningCallback.onFinishedTranslatingText()
    }

    fun translate(){
        sentences.toObservable().observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).subscribeBy  (
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




    fun underLine(behaviour: BottomSheetBehavior<View>) {
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
                        return convertToCharSequence(t,behaviour)
                    }
                }).subscribe(getLinksObserver())

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
                    //getSingleTranslate(idiom,sentenceIndex, sentence)
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
       // bookmarkDataEmitter.insertIndexedSentence(sentenceIndex, sentence ,idiom) //harusnya bukan idiom, tapi sentence
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