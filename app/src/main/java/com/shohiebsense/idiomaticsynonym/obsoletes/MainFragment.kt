package com.shohiebsense.idiomaticsynonym.obsoletes


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.MainCallback
import io.reactivex.disposables.Disposable


/**
 * Created by shohiebsense on 24/06/17.
 */

class MainFragment : Fragment(), MainCallback {

    lateinit var mainCallback : TranslatedAndUntranslatedDataEmitter
    lateinit var fetchedText : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        /*startButton.setOnClickListener{
            AppUtil.navigateToFragment(activity, PdfDisplayFragment::class.java.name)
        }

        bookmarkButton.setOnClickListener {
            startActivity(Intent(act,BookmarkedActivity::class.java))
        }*/
        //AppUtil.makeErrorLog("sizee sampesini translatedIdiom "+ TranslatedAndUntranslatedDataEmitter.translatedIdiomList.size )
        //AppUtil.makeErrorLog("sizee sampesini UNTRANSLATED IDIOM "+ TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.size )

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
