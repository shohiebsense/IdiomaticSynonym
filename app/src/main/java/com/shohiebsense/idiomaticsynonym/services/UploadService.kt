package com.shohiebsense.idiomaticsynonym.services

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.gms.drive.*
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import de.mateware.snacky.Snacky
import java.io.OutputStreamWriter
import java.util.HashSet


/**
 * Created by Shohiebsense on 02/06/2018
 */

class UploadService(val activity: Activity) {

    /**
     * Request code for Google Sign-in
     */
    protected val REQUEST_CODE_SIGN_IN = 0

    /**
     * Request code for the Drive picker
     */
    protected val REQUEST_CODE_OPEN_ITEM = 1

    /**
     * Handles high-level drive functions like sync
     */
    private var mDriveClient: DriveClient? = null

    /**
     * Handle access to Drive resources/files.
     */
    private var mDriveResourceClient: DriveResourceClient? = null

    /**
     * Tracks completion of the drive picker
     */
    private var mOpenItemTaskSource: TaskCompletionSource<DriveId>? = null

    init {
        signIn()
    }

    protected fun signIn() {
        val requiredScopes = HashSet<Scope>(2)
        requiredScopes.add(Drive.SCOPE_FILE)
        requiredScopes.add(Drive.SCOPE_APPFOLDER)
        val signInAccount = GoogleSignIn.getLastSignedInAccount(activity)
        if (signInAccount != null && signInAccount.grantedScopes.containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount)
        } else {
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE)
                    .requestScopes(Drive.SCOPE_APPFOLDER)
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(activity, signInOptions)
            activity.startActivityForResult(googleSignInClient.signInIntent, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun initializeDriveClient(signInAccount: GoogleSignInAccount) {
        mDriveClient = Drive.getDriveClient(activity.applicationContext, signInAccount)
        mDriveResourceClient = Drive.getDriveResourceClient(activity.applicationContext, signInAccount)
        onDriveClientReady()
    }

  fun onDriveClientReady(){
      //createFile()
  }

    private fun createFile(id: Int, name: String) {
        // [START create_file]
        val rootFolderTask = mDriveResourceClient?.getRootFolder()
        val createContentsTask = mDriveResourceClient?.createContents()
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask<DriveFile> { task ->
                    val parent = rootFolderTask?.getResult()
                    val contents = createContentsTask?.getResult()
                    val outputStream = contents?.getOutputStream()
                    OutputStreamWriter(outputStream).use { writer -> writer.write("Hello World!") }

                    val changeSet = MetadataChangeSet.Builder()
                            .setTitle("$name.docx")
                            .setMimeType("text/plain")
                            // .setMimeType("application/vnd.google-apps.document")
                            .setStarred(true)
                            .build()

                    mDriveResourceClient?.createFile(parent!!, changeSet, contents)
                }
                .addOnSuccessListener(activity) { driveFile ->
                    Log.e("shohiebsenseee ", driveFile.getDriveId().toString())
                    showMessage(activity.getString(R.string.file_created) + " " + driveFile.getDriveId().encodeToString())
                    val emitter = BookmarkDataEmitter(activity)
                    emitter.updateUploadId(id.toString(),driveFile.driveId.encodeToString())
                    //finish()
                }
                .addOnFailureListener(activity, { e ->
                    AppUtil.makeErrorLog("unable to create file: "+e.toString())
                    showMessage(activity.getString(R.string.file_create_error))
                    //finish()
                })
        // [END create_file]
    }


    fun upload(id: Int, name: String){
        createFile(id,name)
    }

    protected fun showMessage(message: String) {
        Snacky.builder().setActivity(activity).setText(message).warning().show()
    }

}