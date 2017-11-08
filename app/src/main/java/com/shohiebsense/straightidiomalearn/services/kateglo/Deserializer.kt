package com.shohiebsense.straightidiomalearn.services.kateglo

import com.google.common.collect.Iterables
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shohiebsense.straightidiomalearn.model.api.Synonym
import com.shohiebsense.straightidiomalearn.model.api.SynonymWord
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import java.lang.reflect.Type

/**
 * Created by Shohiebsense on 27/09/2017.
 */

 class Deserializer : JsonDeserializer<Synonym> {


    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Synonym {
        var gson = Gson()

        AppUtil.makeDebugLog(json.toString() + " deserialize Json")

        /*try{
            value = json.asJsonObject.get("kateglo").asJsonObject.get("relation").asJsonObject

        }
        catch (e : Exception){
            AppUtil.makeErrorLog("errorrrs "+e.toString())
        }
       // AppUtil.makeDebugLog("sigg " + value.toString()  )*/


            var entires : Iterable<Map.Entry<String, JsonElement>> = json.asJsonObject.entrySet()

            var synonyms = arrayListOf<SynonymWord>()

             // entire in entires
            for (i in 1 until Iterables.size(entires)) {
                // ...

                AppUtil.makeDebugLog("sigg " + Iterables.get(entires, i).value  )
                synonyms.add(gson.fromJson(Iterables.get(entires, i).value, SynonymWord::class.java))


            }
            var synonym = Synonym(Iterables.get(entires, 0).value.asString, synonyms)

        return synonym
    }

}