package com.shohiebsense.idiomaticsynonym.obsoletes.wordfrequency

import android.app.IntentService
import android.content.Intent

/**
 * Created by Shohiebsense on 06/12/2017.
 */
class WordFrequencyService : IntentService(WordFrequencyService::class.java.name) {
    override fun onHandleIntent(p0: Intent?) {

    }


    /*lateinit var wordFrequencyRetrofit : WordFrequencyInterface

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
                        *//*for(i in 0 .. headers.size()){

                        }*//*

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

    }*/


}

