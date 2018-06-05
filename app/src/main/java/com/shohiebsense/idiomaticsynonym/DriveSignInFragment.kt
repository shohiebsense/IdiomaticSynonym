package com.shohiebsense.idiomaticsynonym

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import org.jetbrains.anko.act
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.drive.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_gplus.*
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * Created by Shohiebsense on 31/05/2018
 */

class DriveSignInFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {

    private val TAG = "GPlusFragent"
    private val RC_SIGN_IN = 0
    private var mGoogleApiClient: GoogleApiClient? = null
    private var signInButton: SignInButton? = null
    private var signOutButton: Button? = null
    private val disconnectButton: Button? = null
    private val signOutView: LinearLayout? = null
    private var mStatusTextView: TextView? = null
    private var mProgressDialog: ProgressDialog? = null
    private var imgProfilePic: ImageView? = null


    private var mDriveClient: DriveClient? = null
    private var mDriveResourceClient: DriveResourceClient? = null
    private var mBitmapToSave: Bitmap? = null

    private val REQUEST_CODE_CAPTURE_IMAGE = 1
    private val REQUEST_CODE_CREATOR = 2

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Drive.SCOPE_FILE)
                .build()

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = GoogleApiClient.Builder(activity!!)
                .enableAutoManage(activity!! /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()


    }


    override fun onStart() {
        super.onStart()

        val opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient)
        if (opr.isDone) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in")
            val result = opr.get()
            handleSignInResult(result)
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog()
            opr.setResultCallback { googleSignInResult ->
                hideProgressDialog()
                handleSignInResult(googleSignInResult)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_gplus, parent, false)

        signInButton = v.findViewById<View>(R.id.sign_in_button) as SignInButton
        signOutButton = v.findViewById<View>(R.id.sign_out_button) as Button
        imgProfilePic = v.findViewById<View>(R.id.img_profile_pic) as ImageView

        mStatusTextView = v.findViewById<View>(R.id.status) as TextView
        signInButton!!.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }


        signOutButton!!.setOnClickListener { Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { updateUI(false) } }

        return v
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e("shohiebsenseee ","equest codee "+requestCode)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)
            mDriveClient = Drive.getDriveClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity!!)!!)
            // Build a drive resource client.
            mDriveResourceClient = Drive.getDriveResourceClient(activity!!, GoogleSignIn.getLastSignedInAccount(activity)!!)
            // Start camera.
           /* startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE)*/
            startActivity(Intent(activity,DriveCreateFileActivity::class.java))
        }
        else if(requestCode == REQUEST_CODE_CAPTURE_IMAGE) {
            Log.i(TAG, "capture image request code")
            // Called after a photo has been taken.
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Image captured successfully.")
                // Store the image data as a bitmap for writing later.
                mBitmapToSave = data?.getExtras()?.get("data") as Bitmap
                saveFileToDrive()
            }
        }
        else if(requestCode == REQUEST_CODE_CREATOR){
        Log.i(TAG, "creator request code")
        // Called after a file is saved to Drive.
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Image successfully saved.")
            mBitmapToSave = null
            // Just start the camera again for another photo.
            startActivityForResult(
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE), REQUEST_CODE_CAPTURE_IMAGE)
        }}
    }

    private fun saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.")
        val image = mBitmapToSave

        mDriveResourceClient!!.createContents()
                .continueWithTask { task -> createFileIntentSender(task.result, image!!) }
                .addOnFailureListener { e -> Log.w(TAG, "Failed to create new contents.", e) }
    }


    private fun handleSignInResult(result: GoogleSignInResult) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess + "  " + result.signInAccount + result.status)
        if (result.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount
            mStatusTextView!!.text = getString(R.string.signed_in_fmt, acct!!.displayName)
            //Similarly you can get the email and photourl using acct.getEmail() and  acct.getPhotoUrl()

            if (acct.photoUrl != null)

                updateUI(true)
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false)
        }
    }


    private fun updateUI(signedIn: Boolean) {
        if (signedIn) {
            signInButton!!.visibility = View.GONE
            signOutButton!!.visibility = View.VISIBLE
        } else {
            mStatusTextView!!.setText(R.string.signed_out)
            signInButton!!.visibility = View.VISIBLE
            signOutButton!!.visibility = View.GONE
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:$connectionResult")
    }

    private fun showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setMessage(getString(R.string.loading))
            mProgressDialog!!.isIndeterminate = true
        }

        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.hide()
        }

    }


    /**
     * Background Async task to load user profile picture from url
     */
    private inner class LoadProfileImage(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg uri: String): Bitmap? {
            val url = uri[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(url).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap?) {

            if (result != null) {


                val resized = Bitmap.createScaledBitmap(result, 200, 200, true)

            }
        }
    }


    private fun createFileIntentSender(driveContents: DriveContents, image: Bitmap): Task<Void> {
        Log.i(TAG, "New contents created.")
        // Get an output stream for the contents.
        val outputStream = driveContents.outputStream
        // Write the bitmap data from it.
        val bitmapStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream)
        try {
            outputStream.write(bitmapStream.toByteArray())
        } catch (e: IOException) {
            Log.w(TAG, "Unable to write file contents.", e)
        }

        // Create the initial metadata - MIME type and title.
        // Note that the user will be able to change the title later.
        val metadataChangeSet = MetadataChangeSet.Builder()
                .setMimeType("image/jpeg")
                .setTitle("Android Photo.png")
                .build()
        // Set up options to configure and display the create file activity.
        val createFileActivityOptions = CreateFileActivityOptions.Builder()
                .setInitialMetadata(metadataChangeSet)
                .setInitialDriveContents(driveContents)
                .build()

        return mDriveClient!!.newCreateFileActivityIntentSender(createFileActivityOptions)
                .continueWith { task ->
                    startIntentSenderForResult(task.result, REQUEST_CODE_CREATOR, null, 0, 0, 0,null)
                    null
                }
    }


}



