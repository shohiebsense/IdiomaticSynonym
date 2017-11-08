package com.shohiebsense.straightidiomalearn.utils

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.shohiebsense.straightidiomalearn.MainActivity
import java.util.regex.Pattern
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import com.shohiebsense.straightidiomalearn.R
import android.graphics.Typeface
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.fragment_experiment.*
import org.jetbrains.anko.bundleOf


/**
 * Created by Shohiebsense on 12/09/2017.
 */

class AppUtil {


    companion object {
        fun navigateToFragment(context : Context, fragmentName : String){
            var intent = Intent(context, MainActivity::class.java)

            intent.putExtra(MainActivity.intentMessage, fragmentName)
            context.startActivity(intent)
        }

        fun makeErrorLog(error: String) {
            Log.e(ConstantsUtil.ERROR_TAG, error)
        }

        fun makeDebugLog(debug: String) {
            Log.e(ConstantsUtil.DEBUG_TAG, debug)
        }

        fun isPdfDocument(extension : String) : Boolean = extension.toLowerCase().endsWith(".pdf")

        fun makeToast(context : Context, text : String){
            Toast.makeText(context,text,Toast.LENGTH_LONG).show()
        }


        fun getHeightOfWindow(activity : Activity) : Int{
            var display = activity.windowManager.defaultDisplay
            var size = Point()
            display.getSize(size)
            return size.y

        }

        fun newSpaceInString(words : String) : String{

            val re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE or Pattern.COMMENTS)
            var reMatcher = re.matcher(words)
            val sentences = words.split("(?<=[.!?])\\s* ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var stringBuilder = StringBuilder()
            for(words in sentences){
                stringBuilder.append(words)
                stringBuilder.append("\n\n")
            }


            return  stringBuilder.toString()
        }

        fun splitSentencesToWords(words : String) : MutableList<String>{

            val re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE or Pattern.COMMENTS)
            var reMatcher = re.matcher(words)
            val sentences = words.split("(?<=[.!?])\\s* ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var stringBuilder =  mutableListOf<String>()
            for(words in sentences){
                stringBuilder.add(words)
            }


            return  stringBuilder
        }

        fun fromHtml(word: String): Spanned {
            val result: Spanned
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                result = Html.fromHtml(word, Html.FROM_HTML_MODE_LEGACY)
            } else {
                result = Html.fromHtml(word)
            }
            return result
        }



        fun findText2(){
            val text = "0123hello9012hello8901hello7890"
            val word = "hello"

            println(text.indexOf(word)) // prints "4"
            println(text.lastIndexOf(word)) // prints "22"

            // find all occurrences forward
                var i = -1
                while ({i = text.indexOf(word, i + 1); i } () != -1) {
                    println(i)
                    i++
                }

            // find all occurrences backward
            while ({i = text.lastIndexOf(word, i - 1); i} () != -1) {
                println(i)
                i++
            } // pr
        }


        inline fun <reified T : Fragment> instanceOf(vararg params: Pair<String, Any>)
                = T::class.java.newInstance().apply {
            arguments = bundleOf(*params)
        }


    }

}


