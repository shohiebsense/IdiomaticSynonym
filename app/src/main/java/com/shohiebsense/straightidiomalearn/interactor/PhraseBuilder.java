package com.shohiebsense.straightidiomalearn.interactor;


import io.reactivex.android.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shohiebsense on 05/07/17.
 */

public class PhraseBuilder {





    void runBuilder(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BUILD_TYPE)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
}
