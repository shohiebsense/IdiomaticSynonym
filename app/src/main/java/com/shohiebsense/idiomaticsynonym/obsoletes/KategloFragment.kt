package com.shohiebsense.idiomaticsynonym.obsoletes

import android.app.Fragment

/**
 * Created by Shohiebsense on 26/09/2017.
 */

class KategloFragment : Fragment() {

    /*var disposable = CompositeDisposable()
    var kategloService : KategloApi? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_kateglo, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kategloService = KategloService.createKategloService()

        AppUtil.makeDebugLog("jalankann")
        kategloService!!
                .getThesaurus("json","sedikit")
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
    }*/



}