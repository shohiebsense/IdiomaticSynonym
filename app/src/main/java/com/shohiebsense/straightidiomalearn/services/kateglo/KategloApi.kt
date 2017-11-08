package com.shohiebsense.straightidiomalearn.services.kateglo

import com.shohiebsense.straightidiomalearn.model.api.Thesaurus
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Shohiebsense on 25/09/2017.
 */

interface KategloApi{





    @GET("api.php?")
    fun getThesaurus(@Query("format") format : String, @Query("phrase") phrase : String) : Observable<Thesaurus>


}
