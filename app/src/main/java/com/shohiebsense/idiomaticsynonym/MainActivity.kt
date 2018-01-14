package com.shohiebsense.idiomaticsynonym

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.MainFragment
import com.shohiebsense.idiomaticsynonym.view.fragment.UnderliningFragment
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.DatabaseCallback
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.PdfDisplayCallback
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import java.io.File

class MainActivity : AppCompatActivity(), PdfDisplayCallback {
    override fun onFinishedFetchingPdf(fetchedText: MutableList<String>, name: String) {
    }

    override fun onLoadingPdf() {
    }

    override fun onErrorLoadingPdf() {
    }

    override fun onFinishedLoadingPdf(file: File) {
    }

    override fun onFetchingPdf() {
    }


    //bikin interface yang load pdf. extract textnya.
    //bikin query per model. test
    //bikin clickablespan dengan string multi

    internal var helloStringObservable: Observable<String>? = null
    internal var helloStringObserver : Observer<String>? = null;


    companion object {
        @JvmStatic var INTENT_MESSAGE = "INTENT_MESSAGE"
        @JvmStatic var INTENT_FETCHED_TEXT = "FETCHED_TEXT_MESSAGE"
        @JvmStatic val INTENT_FILENAME = "INTENT_FILENAME"
    }

    internal var anuu = listOf(1,2,3,4)
    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtil.unzip(applicationContext)

        setContentView(R.layout.activity_main)


        //kita liat sizenya
       // AppUtil.makeErrorLog("sizee oncreate translatedIdiom "+ TranslatedAndUntranslatedDataEmitter.translatedIdiomList.size )
       // AppUtil.makeErrorLog("sizee oncreate untranslatedIdiom "+ TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.size )

        //TranslatedAndUntranslatedDataEmitter(this, this ).getAll()
        init()

        navigateToFragment()
        addFragment(fragment, R.id.fragmentFrameLayout)
        //LoadFragment().loadPdf(this);


        //copy()
        //AppUtil.isExists(this, "2.png")


    }



    fun navigateToFragment(){

        var intentMessage : String? = intent.getStringExtra(INTENT_MESSAGE)

        //DEVELOPMENT
        //if(intentMessage == null) intentMessage = UnderliningFragment::class.java.name

        if(intentMessage == null) intentMessage = MainFragment::class.java.name


        //development only
        //if(intentMessage == null) intentMessage = TranslatedDisplayFragment::class.java.name
        else{
            AppUtil.makeDebugLog("navigate to fragment "+intentMessage)
        }

       // var clazz : Class<*> = Class.forName(INTENT_MESSAGE)
        //var ctor : Constructor<*> =  clazz.getConstructor(String::class.java)



        fragment = Class.forName(intentMessage).getConstructor().newInstance() as Fragment
        if(intentMessage.equals(UnderliningFragment::class.java.name)){
            var fetchedTextMessage : ArrayList<String>? = intent.getStringArrayListExtra(INTENT_FETCHED_TEXT)
            val fileName = intent.getStringExtra(INTENT_FILENAME)
            fragment = UnderliningFragment.newInstance(fetchedTextMessage,fileName)
        }
       /* else{
            AppUtil.makeDebugLog("fragment is not null")
            fragment = ctor.newInstance() as Fragment
        }*/


//        when(INTENT_MESSAGE){
//            0 -> fragment = MainFragment()
//            1 -> fragment = PdfDisplayFragment()
//            2 -> fragment = LoadFragment()
//        }


    }


    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun addFragment(){
        fragmentManager.inTransaction { add(R.id.fragmentFrameLayout, fragment) }
    }

    fun addFragment(fragment : Fragment, frameId : Int){
        fragmentManager.inTransaction { add(frameId, fragment) }
    }


    fun replaceFragment(fragment : Fragment, frameId : Int){
        fragmentManager.inTransaction { replace(frameId, fragment) }
    }

    fun init(){
        val list = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        val myObservable = Observable.create(ObservableOnSubscribe<String> { e ->
            e.onNext("au")
            e.onComplete()
        })

        val myObserver = object : Observer<String> {

            override fun onComplete() {
                Toast.makeText(this@MainActivity, "onCOmpleted", Toast.LENGTH_LONG).show()
            }

            override fun onError(e: Throwable) {

            }

            override fun onSubscribe(@NonNull d: Disposable) {

            }

            override fun onNext(text: String) {
            }
        }

        myObservable.subscribe(myObserver)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
        //For development, uncpmment

        //bisa aja langsung kasih query, kasih loading ga usah balik ke splash

    }

    var fetcCallback = object : DatabaseCallback {
        override fun onFetchedBoth() {

        }

        override fun onFetchingData(idiomMode: Int) {
        }

        override fun onErrorFetchingData() {
        }

        override fun onFetchedTranslatedData() {
        }

        override fun onFetchedUntranslatedData() {
            AppUtil.makeDebugLog("beres translasi")
            navigateToFragment()
            addFragment()
        }
    }


    override fun onBackPressed() {
        return
    }
}