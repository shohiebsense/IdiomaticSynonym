package com.shohiebsense.idiomaticsynonym

import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.shohiebsense.idiomaticsynonym.view.fragment.UnderliningFragment

class UnderliningActivity : AppCompatActivity() {

    companion object {
        val INTENT_MESSAGE = "INTENT_MESSAGE"
        val INTENT_FILENAME = "INTENT_FILENAME"
        val INTENT_ID = "INTENT_ID"
    }


    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_underlining)
        if(intent != null)
        navigateFragment()
        addFragment(fragment)
    }

    fun navigateFragment(){
        var intentMessage : String? = intent.getStringExtra(INTENT_MESSAGE)

        //DEVELOPMENT
        //if(intentMessage == null) intentMessage = UnderliningFragment::class.java.name

        //development only
        //if(intentMessage == null) intentMessage = TranslatedDisplayFragment::class.java.name


        // var clazz : Class<*> = Class.forName(INTENT_MESSAGE)
        //var ctor : Constructor<*> =  clazz.getConstructor(String::class.java)



        fragment = Class.forName(intentMessage).getConstructor().newInstance() as Fragment
        if(intentMessage.equals(UnderliningFragment::class.java.name)){
           // var fetchedTextMessage : ArrayList<String>? = intent.getStringArrayListExtra(INTENT_FETCHED_TEXT) development only
            var fetchedTextMessage : ArrayList<String>? = arrayListOf()
            val fileName = intent.getStringExtra(INTENT_FILENAME)
            val lastId = intent.getIntExtra(INTENT_ID,-1)
            fragment = UnderliningFragment.newInstance(fetchedTextMessage,fileName,lastId)
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun addFragment(fragment : Fragment){
        addFragment(fragment, R.id.fragmentFrameLayout)
    }

    fun addFragment(fragment : Fragment, frameId : Int){
        fragmentManager.inTransaction { add(frameId, fragment) }
    }


    fun replaceFragment(fragment : Fragment, frameId : Int){
        fragmentManager.inTransaction { replace(frameId, fragment) }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()
    }



}
