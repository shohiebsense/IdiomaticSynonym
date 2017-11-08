package com.shohiebsense.straightidiomalearn.view.fragment.fetchedtextdisplay

import android.app.Fragment
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.straightidiomalearn.MainActivity
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.services.pdfFetchers.PdfFetcher
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback
import kotlinx.android.synthetic.main.fragment_fetchedtext.*

/**
 * Created by Shohiebsense on 21/10/2017.
 *
 * susunan teks masih salah
 * cek string maksimum`
 *
 * masih lama
 */
class FetchedTextFragment : Fragment(), FetchedTextCallback {

    lateinit var fetchedText : ArrayList<String>
    lateinit var pdfFetcher : PdfFetcher

    companion object {
        fun newInstance(name : ArrayList<String>?) :FetchedTextFragment{
            val args = Bundle()
            args.putStringArrayList(MainActivity.fetchedTextMessage, name)
            val fragment = FetchedTextFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchedText = arguments.getStringArrayList(MainActivity.fetchedTextMessage)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fetchedtext, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  DatabaseDataEmitter(activity, this).getAll()

        pdfFetcher = PdfFetcher(activity, this)
        pdfFetcher.translate(fetchedText)
        if(DatabaseDataEmitter.translatedIdiomList.size > 0 && DatabaseDataEmitter.untranslatedIdiomList.size > 0){
            pdfFetcher.getUnderlineTheFetchedText(fetchedText)
        }

    }

    override fun onTranslatingText() {
       // toggleViews(PdfFetcher.STATUS_LOADING)
    }
    override fun onFinishedTranslatingText() {
       // toggleViews(PdfFetcher.STATUS_TRANSLATED)
        //pdfFetcher.getUnderlineTheFetchedText()
    }

    override fun onErrorTranslatingText() {
      //  toggleErrorViews(PdfFetcher.ERROR_TRANSLATE)
    }


    override fun onErrorUnderliningText(decoratedSpan: SpannableString) {
        avLoadingIndicatorView.visibility = View.GONE
        textFetchedScrollView.visibility = View.VISIBLE
    }

    override fun onFinishedUnderliningText(decoratedSpan: SpannableString) {
        avLoadingIndicatorView.visibility = View.GONE
        textFetchedScrollView.visibility = View.VISIBLE
        textFetchedTextView.movementMethod = LinkMovementMethod.getInstance()
        textFetchedTextView.setText(decoratedSpan)
    }


    override fun onErrorFetchingDatabase() {
       // toggleErrorViews(PdfFetcher.ERROR_FETCH)
    }

    override fun onFetchingDatabase() {
       // toggleViews(PdfFetcher.STATUS_LOADING)
    }

    override fun onFinishedFetchingTranslatedDatabase(translatedIdiomList: MutableList<TranslatedIdiom>) {
        //toggleViews(PdfFetcher.STATUS_FETCHED_DB)
        //commented due DatabaseDataEmitter had mutableList
       // pdfFetcher.translatedIdiomList = translatedIdiomList


        AppUtil.makeErrorLog("beres translatedIdiom list" + translatedIdiomList.size)
    }

    override fun onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList: MutableList<UntranslatedIdiom>) {
        //  toggleViews(PdfFetcher.STATUS_FETCHED_DB)

        //pdfFetcher.untranslatedIdiomList = untranslatedIdiomList
        //pdfFetcher.getUnderlineTheFetchedText(fetchedText)

        AppUtil.makeErrorLog("beres untranslated list "+ untranslatedIdiomList.size)
        //pdfFetcher.getUnderlineTheFetchedText()


    }


    override fun onFindingTranslatedIdiom() {
    }

    override fun onFinishedFindingTranslatedIdiom(anuu: String, decoratedSpan: SpannableString, clickableSpan: ClickableSpan) {
        pdfFetcher.getUnderlineTheFetchedWithUntranslatedText(anuu, decoratedSpan, clickableSpan)
    }

    override fun onFindingUntranslatedIdiom() {
    }

    override fun onFinishedUntranslatedIdiom() {
    }

    override fun onClickedIdiomText(idiomText : String) {
        AppUtil.makeErrorLog("idioms - relations "+idiomText)
    }

    override fun onErrorClickedIdiomText() {
        AppUtil.makeErrorLog("Translating idiom finiished")
    }








}