package com.shohiebsense.idiomaticsynonym.services.kateglo

import com.shohiebsense.idiomaticsynonym.model.api.Synonym
import com.shohiebsense.idiomaticsynonym.model.api.SynonymWord
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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

class KategloService {
    var BASE_URL = "http://kateglo.com/api.php?"
    val JSON = MediaType.parse("application/json; charset=utf-8")
    fun createKategloService(word : String): Response? {
        // var gson = GsonBuilder().registerTypeAdapter(Synonym::class.java, Deserializer()).create()

        var client = OkHttpClient()
        var request = Request.Builder()
                .url(BASE_URL + "format=json&phrase="+word)
                .get()
                .build()

        var response = client.newCall(request).execute()
        return response
    }


    fun getSynonyms(word : String) : List<SynonymWord>{
        var response = createKategloService(word)
        var syonymObjects = mutableListOf<Synonym>()
        var syonymWordObjects = mutableListOf<SynonymWord>()

        if(!response!!.isSuccessful){
            return syonymWordObjects
        }


        var responseString = response.body()!!.string()
        var jsonObject = JSONObject(responseString)
        var kategloObject = jsonObject.getJSONObject("kateglo")
        var relationObject = kategloObject.getJSONObject("relation")
        var sumOfRelation = relationObject.getInt("relation_reverse")

        var synonymObject = relationObject.getJSONObject("s")


        for(i in 0 .. sumOfRelation){
            var synonymWordObject = synonymObject.getJSONObject(i.toString())
            var synonymWord = SynonymWord(
                    synonymWordObject.getString("root_phrase"),
                    synonymWordObject.getString("related_phrase"),
                    synonymWordObject.getString("rel_type"),
                    synonymWordObject.getString("rel_type_name"),
                    synonymWordObject.getString("lex_class")
            )

            syonymWordObjects.add(synonymWord)
        }


        return syonymWordObjects

    }

    fun getResponse(word : String) : String {
        return createKategloService(word)!!.body()!!.string()
    }



    fun getSynonymStrings(word : String, listener : KategloListener) : List<String>{

        var syonymWordObjects = mutableListOf<String>()
        var callable = object : Callable<MutableList<String>>{
            override fun call(): MutableList<String>? {
                val response = createKategloService(word)
                if (response!!.isSuccessful) {
                    val responseString = response.body()!!.string()
                    val jsonObject = JSONObject(responseString)
                    val kategloObject = jsonObject.getJSONObject("kateglo")
                    val relationObject = kategloObject.getJSONObject("relation")
                    val sumOfRelation = relationObject.getInt("relation_reverse")

                    val synonymObject = relationObject.getJSONObject("s")


                    for (i in 0..sumOfRelation) {
                        val synonymWordObject = synonymObject.getJSONObject(i.toString())
                        val synonymWord = synonymWordObject.getString("related_phrase")
                        AppUtil.makeDebugLog("syonym : " + synonymWord)
                        syonymWordObjects.add(synonymWord)
                    }
                }
                return syonymWordObjects
            }

        }


        var observer = object : Observer<MutableList<String>> {
            override fun onNext(t: MutableList<String>) {
                listener.onGetSynonyms(syonymWordObjects)
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onComplete() {


            }

            override fun onError(e: Throwable) {

            }

        }
        Observable.fromCallable(callable).subscribeOn(Schedulers.io()).unsubscribeOn(AndroidSchedulers.mainThread()).subscribe(observer)
        //Observable.fromCallable(callable).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(observer)

        AppUtil.makeDebugLog(" sizee "+syonymWordObjects.size)
        return syonymWordObjects

    }


    interface KategloListener {
        fun onGetSynonyms(syonyms : MutableList<String>)
    }


}
