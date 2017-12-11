package com.shohiebsense.straightidiomalearn.services.wordfrequency

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Created by Shohiebsense on 06/12/2017.
 */
interface WordFrequencyInterface {


    @Streaming
    @GET
    fun getTxtFile(@Url fileUrl : String) : Single<Response<ResponseBody>>


}