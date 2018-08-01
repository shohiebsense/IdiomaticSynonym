package com.shohiebsense.idiomaticsynonym.obsoletes

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import com.shohiebsense.idiomaticsynonym.R

class DriveSignInActivity : AppCompatActivity() {

    companion object {
        val INTENT_ID = "englishid"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in_example)
        val id = intent.getIntExtra(INTENT_ID,0)

        val fm = supportFragmentManager
        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)


        if (fragment == null) {
            fragment = DriveSignInFragment.newInstance(id)
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()


        }


    }
}
