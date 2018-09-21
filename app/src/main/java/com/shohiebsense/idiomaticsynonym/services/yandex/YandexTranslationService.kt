package com.shohiebsense.idiomaticsynonym.services.yandex

import android.content.Context
import android.text.SpannableStringBuilder
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.Callable


/**
 * Created by Shohiebsense on 24/12/2017.
 */

class YandexTranslationService(val context: Context, val listener: YandexListener) {
    var BASE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?"
    var API_KEY = "trnsl.1.1.20170624T065148Z.2b459b88c8430b8f.6f17fd682b21e183634a4346b6065199184c589f"
    val JSON = MediaType.parse("application/json; charset=utf-8")

    fun translateExe(word : String): Response? {
        var client = OkHttpClient.Builder().retryOnConnectionFailure(true)
                .build()
        var request = Request.Builder()
                .url(BASE_URL + "key=$API_KEY&lang=en-id&text=$word")
                .addHeader("Connection","close")
                .get()
                .build()
        if(AppUtil.checkInternetConnection(context)){
            return client.newCall(request).execute()
        }
        else{
            AppUtil.makeErrorLog("error connection")
            listener.onErrorConnection()
            return null
        }
    }

    fun getSingleTranslate(word : String) {
        var callable = object : Callable<String>{
            override fun call(): String? {
                var response = translateExe(word)
                if(response != null){
                    if (response.isSuccessful) {
                        val responseString = response.body()!!.string()
                        val jsonObject = JSONObject(responseString)
                        AppUtil.makeErrorLog("jsonobjecttt "+jsonObject.toString())
                        val textNode = jsonObject.getJSONArray("text")
                        val translationText = textNode.getString(0)
                        return translationText
                    }
                }
                return ""
            }
        }

        var observer = object : Observer<String> {
            override fun onNext(t: String) {
                listener.onGetTranslation(t)
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onComplete() {


            }

            override fun onError(e: Throwable) {
                AppUtil.makeErrorLog("error during yandex "+e.toString())
            }

        }
        Observable.fromCallable(callable).subscribeOn(Schedulers.io()).unsubscribeOn(AndroidSchedulers.mainThread()).subscribe(observer)
        //Observable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    fun getSingleTranslate(observer: Observer<String>, text: MutableList<String>) {
        text.toObservable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe{
            val response = translateExe(it)
            if(response != null){
                if (response.isSuccessful) {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    AppUtil.makeErrorLog("dapet gaa nya "+jsonObject.toString()+ "  lang"+jsonObject.getString("lang"))
                    val textNode = jsonObject.getJSONArray("text")
                    val translationText = textNode.getString(0)
                    AppUtil.makeErrorLog("dapet ga oyyy "+translationText)
                    observer.onNext(translationText)
                }
            }

        }
        //Observable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)
    }

    fun getIdiomTranslate(observer: Observer<String>, text: String) {
        Observable.create<String> {
                val response = translateExe(text)
            if(response != null){
                if (response.isSuccessful) {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    AppUtil.makeErrorLog("dapet gaa nya "+jsonObject.toString()+ "  lang"+jsonObject.getString("lang"))
                    val textNode = jsonObject.getJSONArray("text")
                    val translationText = textNode.getString(0)
                    AppUtil.makeErrorLog("dapet ga oyyy "+translationText)
                    observer.onNext(translationText)
                }
            }
        }.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe()
    }

    fun bulkTranslate(pdfText : String) : String{
        val response = translateExe(pdfText)
        if(response != null){
            if (response.isSuccessful) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val textNode = jsonObject.getJSONArray("text")
                var translationText = textNode.getString(0).trim()

                return translationText
            }
        }

        return ""
    }



    fun translate(it: String, index: Int) : String {
        //commented due to development, uncomment again.
        //val detections = translateService.detect(ImmutableList.of(it))
        val response = translateExe(it)
        if(response != null){
            if (response.isSuccessful) {
                val responseString = response.body()!!.string()
                val jsonObject = JSONObject(responseString)
                val textNode = jsonObject.getJSONArray("text")
                var translationText = textNode.getString(0).trim()

                return  translationText
            }
        }
        return ""
    }





    interface YandexListener {
        fun onGetTranslation(text : String)
        fun onErrorConnection()
    }


}
