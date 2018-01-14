package com.shohiebsense.idiomaticsynonym.obsoletes

/**
 * Created by Shohiebsense on 03/01/2018.
 */

class TranslatedAndUntranslatedDataEmitterObsolete {

    /*var unTranslatedConsumer = object : Observer<UntranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            databaseCallback.onFetchingData(FETCHING_UNTRANSLATED_IDIOM_MODE)
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            databaseCallback.onErrorFetchingData()


        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd untranslated") //last
            databaseCallback.onFetchedUntranslatedData()
        }

        override fun onNext(untranslatedIdiom: UntranslatedIdiom) {
            untranslatedIdiomList.add(untranslatedIdiom)
           // AppUtil.makeErrorLog("untranslated " + untranslatedIdiomList.size)
        }
    }*/


    /* var translatedConsumer = object : Observer<TranslatedIdiom> {
        override fun onSubscribe(d: Disposable) {
            AppUtil.makeDebugLog("subscribe ss ") //first
            databaseCallback.onFetchingData(FETCHING_TRANSLATED_IDIOM_MODE
            )
        }

        override fun onError(e: Throwable) {
            AppUtil.makeDebugLog("errorr ss")
            databaseCallback.onErrorFetchingData()

        }

        override fun onComplete() {
            AppUtil.makeDebugLog("completeedd ss") //last
            databaseCallback.onFetchedTranslatedData()

        }

        override fun onNext(translatedIdiom: TranslatedIdiom) {
            translatedIdiomList.add(translatedIdiom)
          //  AppUtil.makeErrorLog("translatedidiom " + translatedIdiomList.size)

        }
    }*/
}
