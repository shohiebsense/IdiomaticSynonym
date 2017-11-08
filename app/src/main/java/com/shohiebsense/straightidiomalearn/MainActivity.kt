package com.shohiebsense.straightidiomalearn

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.MainFragment
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchCallback
import com.shohiebsense.straightidiomalearn.view.fragment.fetchedtextdisplay.FetchedTextFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import java.io.File
import java.lang.reflect.Constructor

class MainActivity : AppCompatActivity(), FetchCallback {
    override fun onFinishedFetchingPdf(fetchedText: MutableList<String>) {
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
        @JvmStatic var intentMessage = "INTENT_MESSAGE"
        @JvmStatic var fetchedTextMessage = "FETCHED_TEXT_MESSAGE"
    }

    internal var anuu = listOf(1,2,3,4)
    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //kita liat sizenya
        AppUtil.makeErrorLog("sizee oncreate translatedIdiom "+DatabaseDataEmitter.translatedIdiomList.size )
        AppUtil.makeErrorLog("sizee oncreate untranslatedIdiom "+DatabaseDataEmitter.untranslatedIdiomList.size )

        //DatabaseDataEmitter(this, this ).getAll()
        init()

        navigateToFragment()
        addFragment(fragment, R.id.fragmentFrameLayout)
        //LoadFragment().loadPdf(this);




    }


    fun navigateToFragment(){

        var intentMessage : String? = intent.getStringExtra(intentMessage)
        var fetchedTextMessage : ArrayList<String>? = intent.getStringArrayListExtra(fetchedTextMessage)

        if(intentMessage == null) intentMessage = MainFragment::class.java.name
        else{
            AppUtil.makeDebugLog("navigate to fragment "+intentMessage)
        }
        if(fetchedTextMessage == null) {
            AppUtil.makeErrorLog("error : fetchedtextnull or empty")
            ///error
           // return

        }

        AppUtil.makeDebugLog(intentMessage+" apa sih hasilnya")
       // var clazz : Class<*> = Class.forName(intentMessage)
        //var ctor : Constructor<*> =  clazz.getConstructor(String::class.java)



        fragment = Class.forName(intentMessage).getConstructor().newInstance() as Fragment
        if(intentMessage.equals(FetchedTextFragment::class.java.name)){
            fragment = FetchedTextFragment.newInstance(fetchedTextMessage)
        }
       /* else{
            AppUtil.makeDebugLog("fragment is not null")
            fragment = ctor.newInstance() as Fragment
        }*/


//        when(intentMessage){
//            0 -> fragment = MainFragment()
//            1 -> fragment = FetchFragment()
//            2 -> fragment = LoadFragment()
//        }


    }


    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
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
}
