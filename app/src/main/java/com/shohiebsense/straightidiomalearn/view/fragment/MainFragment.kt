package com.shohiebsense.straightidiomalearn.view.fragment


import android.app.Fragment
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.MainCallback
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.view.fragment.pdfdisplay.PdfDisplayFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * Created by shohiebsense on 24/06/17.
 */

class MainFragment : Fragment(), MainCallback {

    lateinit var mainCallback : DatabaseDataEmitter
    lateinit var fetchedText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton.setOnClickListener{
            AppUtil.navigateToFragment(activity, PdfDisplayFragment::class.java.name)
        }
        AppUtil.makeErrorLog("sizee sampesini translatedIdiom "+DatabaseDataEmitter.translatedIdiomList.size )
        AppUtil.makeErrorLog("sizee sampesini UNTRANSLATED IDIOM "+DatabaseDataEmitter.untranslatedIdiomList.size )

        getAllTranslatedWord()
    }

    fun getAllTranslatedWord(){
    }

    fun findText(context : Context, myObserver : Observer<SpannableString>){
        var dolor ="dolor"
        var startIndex = fetchedText.toLowerCase().indexOf(dolor)

        AppUtil.makeDebugLog("indexx ke "+startIndex)
        var endIndex = startIndex + dolor.length
        var decoratedSpan = SpannableString(fetchedText)
        var clickableSpan = object : ClickableSpan(){
            override fun onClick(p0: View?) {
                AppUtil.makeDebugLog("kok ga keluarr")
                Toast.makeText(context, "anuu "+dolor, Toast.LENGTH_LONG).show()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText =false
            }
        }
        val myObservable = Observable.create(ObservableOnSubscribe<SpannableString> { e->

            while(startIndex >=0 ){

                AppUtil.makeDebugLog("aaiawei "+startIndex)
                decoratedSpan.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondaryDarkColor)), 0, decoratedSpan.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                decoratedSpan.setSpan(StyleSpan(Typeface.BOLD),startIndex,startIndex+dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                decoratedSpan.setSpan(object: ClickableSpan(){
                    override fun onClick(p0: View?) {
                        AppUtil.makeDebugLog("kok ga keluarr")
                        Toast.makeText(context, "anuu "+dolor, Toast.LENGTH_LONG).show()
                    }

                    override fun getUnderlying(): CharacterStyle {
                        return super.getUnderlying()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }, startIndex, startIndex+dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                decoratedSpan.setSpan(clickableSpan, startIndex, startIndex + dolor.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                startIndex = fetchedText.toLowerCase().indexOf(dolor, startIndex + 1)
            }
            e.onNext(decoratedSpan)
            e.onComplete()
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())



        myObservable.subscribe(myObserver)
    }


    override fun onFetchingSuccess(disposable: Disposable) {
        AppUtil.makeDebugLog("subscribe ss ") //first
    }

    override fun onFetchProcess(translatedIdiom: TranslatedIdiom) {
        AppUtil.makeErrorLog("shohieb "+ translatedIdiom.idiom)
    }

    override fun onFetchingFailed(throwable: Throwable) {
        AppUtil.makeDebugLog("errorr ss")
    }

    override fun pnFetchingCompleted() {
        AppUtil.makeDebugLog("completeedd ss") //last
    }
}
