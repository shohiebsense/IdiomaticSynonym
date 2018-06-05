package com.shohiebsense.idiomaticsynonym.obsoletes


/**
 * Created by Shohiebsense on 02/06/2018
 */

class TrnaslateService {


    /*fun translate(observer : Observer<SpannableStringBuilder>, mutableListArray: List<String>, indices : ArrayList<Int>)  {
        //commented due to development, uncomment again.

        Observable.create<SpannableStringBuilder> { observerr ->

            options = TranslateOptions.newBuilder()
                    .setApiKey(context.getString(R.string.API_TRANSLATE_KEY))
                    .build()
            translateService = options.service

            mutableListArray.forEachIndexed { index, it ->

                val language = translateService.detect(it).language.toLowerCase()

                val detections = translateService.detect(ImmutableList.of(it))
                AppUtil.makeDebugLog("Language(s) detected:")
                for (detection in detections) {
                    AppUtil.makeDebugLog(detection.toString())
                }
                AppUtil.makeDebugLog("translatt: " + language)
                if (language.equals("en")) {
                    AppUtil.makeDebugLog("before translation " + it)
                    var translation = translateService.translate(it,
                            Translate.TranslateOption.targetLanguage("id"), model)

                    if (translation != null) {
                        spannableStringBuilder = SpannableStringBuilder(translation.translatedText)

                        if (flagged) {
                            spannableStringBuilder.setSpan(StyleSpan(Typeface.BOLD), 0, translation.translatedText.lastIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                        }

                        return spannableStringBuilder

                    }
                }
            }
                    //AppUtil.makeDebugLog("hasil translasi : \n" + translation.translatedText)
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(observer)
    }*/
}