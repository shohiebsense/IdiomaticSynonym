package com.shohiebsense.straightidiomalearn.services.kateglo

import com.google.gson.GsonBuilder
import com.shohiebsense.straightidiomalearn.model.api.Synonym
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Shohiebsense on 25/09/2017.
 */

class KategloService {

    companion object {

        var BASE_URL = "http://kateglo.com/"

        fun createKategloService() : KategloApi {
            var gson = GsonBuilder().registerTypeAdapter(Synonym::class.java, Deserializer()).create()



            var builder : Retrofit.Builder =
                    Retrofit.Builder()
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .baseUrl(BASE_URL)


            /*if(TextUtils.isEmpty(token)){
                var client = OkHttpClient.Builder()
                        .addInterceptor {
                            chain ->
                            var request = chain.request()
                            var newRequest = request.newBuilder().addHeader("anu","").build()
                            chain.proceed(newRequest)
                        }.build()
            }*/
            return builder.build().create(KategloApi::class.java)
        }
    }
}
