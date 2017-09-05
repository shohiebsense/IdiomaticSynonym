package com.shohiebsense.straightidiomalearn

import android.database.Observable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import io.reactivex.Observer
import io.reactivex.annotations.NonNull
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
import org.androidannotations.annotations.EActivity

@EActivity(R.layout.activity_main)
class MainActivity : AppCompatActivity() {

    internal var helloStringObservable: Observable<String>? = null
    internal var helloStringObserver : Observer<String>? = null;
    internal var editText : EditText? = null;

    internal var anuu = listOf(1,2,3,4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.helloEditText) as EditText

    }


    fun init(){
        val list = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon")


    }
}
