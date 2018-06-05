package com.shohiebsense.idiomaticsynonym

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment

class DriveSignInActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in_example)

        val fm = supportFragmentManager
        var fragment: Fragment? = fm.findFragmentById(R.id.fragment_container)


        if (fragment == null) {
            fragment = DriveSignInFragment()
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit()


        }


    }
}
