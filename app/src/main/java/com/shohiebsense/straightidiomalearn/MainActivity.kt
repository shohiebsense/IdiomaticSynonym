package com.shohiebsense.straightidiomalearn

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.DatabaseCallback
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchCallback
import com.shohiebsense.straightidiomalearn.view.fragment.fetchedtextdisplay.UnderliningFragment
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.Disposable
import java.io.File

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
        AppUtil.unzip(applicationContext)

        setContentView(R.layout.activity_main)


        //kita liat sizenya
        AppUtil.makeErrorLog("sizee oncreate translatedIdiom "+DatabaseDataEmitter.translatedIdiomList.size )
        AppUtil.makeErrorLog("sizee oncreate untranslatedIdiom "+DatabaseDataEmitter.untranslatedIdiomList.size )

        //DatabaseDataEmitter(this, this ).getAll()
        init()

        navigateToFragment()
        addFragment(fragment, R.id.fragmentFrameLayout)
        //LoadFragment().loadPdf(this);


        //copy()
        //AppUtil.isExists(this, "2.png")


    }

    fun  copy(){
        AppUtil.unzip(this)
    }


    fun navigateToFragment(){

        var intentMessage : String? = intent.getStringExtra(intentMessage)
        var fetchedTextMessage : ArrayList<String>? = intent.getStringArrayListExtra(fetchedTextMessage)

        //DEVELOPMENT
        if(intentMessage == null) intentMessage = UnderliningFragment::class.java.name

        //if(intentMessage == null) intentMessage = PdfDisplayFragment::class.java.name
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
        if(intentMessage.equals(UnderliningFragment::class.java.name)){
            fragment = UnderliningFragment.newInstance(fetchedTextMessage)
        }
       /* else{
            AppUtil.makeDebugLog("fragment is not null")
            fragment = ctor.newInstance() as Fragment
        }*/


//        when(intentMessage){
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
}