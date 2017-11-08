package com.shohiebsense.straightidiomalearn.view.fragment.apidisplay

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.api.Thesaurus
import com.shohiebsense.straightidiomalearn.services.kateglo.KategloApi
import com.shohiebsense.straightidiomalearn.services.kateglo.KategloService
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

/**
 * Created by Shohiebsense on 26/09/2017.
 */

class KategloFragment : Fragment() {

    var disposable = CompositeDisposable()
    var kategloService : KategloApi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_kateglo, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kategloService = KategloService.createKategloService()

        AppUtil.makeDebugLog("jalankann")
        kategloService!!
                .getThesaurus("json","perahu")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        object : DisposableObserver<Thesaurus>() {

                            override fun onComplete() {
                                AppUtil.makeDebugLog("komplittt ")

                            }

                            override fun onError(e: Throwable) {
                                AppUtil.makeErrorLog(e.toString())
                            }

                            override fun onNext(contributors: Thesaurus) {
                                AppUtil.makeDebugLog("dapatt " + contributors.kateglo.relation.synonym.synonymwords.size)
                            }
                        });
    }



}