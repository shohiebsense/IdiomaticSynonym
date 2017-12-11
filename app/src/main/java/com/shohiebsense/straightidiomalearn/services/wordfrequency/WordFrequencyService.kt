package com.shohiebsense.straightidiomalearn.services.wordfrequency

import android.app.IntentService
import android.content.Intent
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

/**
 * Created by Shohiebsense on 06/12/2017.
 */
class WordFrequencyService : IntentService(WordFrequencyService::class.java.name) {


    lateinit var wordFrequencyRetrofit : WordFrequencyInterface

    override fun onHandleIntent(p0: Intent?) {
    }

    companion object {
        val WORD_FREQUENCY_ENDPOINT = "https://github.com/hermitdave/FrequencyWords/tree/master/content/2016/id"
        val TXT_50K = "id_50k.txt"
        val TXT_FULL = "id_full.txt"

    }


    fun getFile(){
        wordFrequencyRetrofit = Retrofit.Builder()
                .baseUrl(WORD_FREQUENCY_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(WordFrequencyInterface::class.java)

        wordFrequencyRetrofit.getTxtFile(TXT_50K)
                .flatMap(Function<Response<ResponseBody>, Single<File>>
                { response: Response<ResponseBody> ->
                    Single.create{
                        singleEmitter: SingleEmitter<File> ->
                        var headers = response.headers()
                        /*for(i in 0 .. headers.size()){

                        }*/

                        //val bufferedSource = response.body()!!.source()
                        val inputStream = response.body()!!.byteStream()

                        val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                        var total = StringBuilder()
                        var line = ""


                        while({line = bufferedReader.readLine(); line}().isEmpty()){
                            total.append(line)
                        }



                    }

                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<File> {
                    override fun onSuccess(t: File) {

                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                })

    }


}

