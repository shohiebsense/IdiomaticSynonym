package com.shohiebsense.idiomaticsynonym.services.kateglo

import com.google.common.collect.Iterables
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.shohiebsense.idiomaticsynonym.model.api.Synonym
import com.shohiebsense.idiomaticsynonym.model.api.SynonymWord
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
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
            var synonym : Synonym

             // entire in entires
            for (i in 1 until Iterables.size(entires)) {
                // ...

                synonyms.add(gson.fromJson(Iterables.get(entires,i).value, SynonymWord::class.java))
                AppUtil.makeDebugLog("added  " + synonyms.size)
                break
            }
            synonym = Synonym(synonyms,Iterables.get(entires, 0).value.toString())

        return synonym
    }

}