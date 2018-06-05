package com.shohiebsense.idiomaticsynonym

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.tasks.Tasks
import java.io.OutputStreamWriter

class DriveCreateFileActivity  : BaseDemoActivity() {
    private val TAG = "CreateFileActivity"

    protected override fun onDriveClientReady() {
        createFile()
    }

    private fun createFile() {
        // [START create_file]
        val rootFolderTask = getDriveResourceClient().getRootFolder()
        val createContentsTask = getDriveResourceClient().createContents()
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask<DriveFile> { task ->
                    val parent = rootFolderTask.getResult()
                    val contents = createContentsTask.getResult()
                    val outputStream = contents.getOutputStream()
                    OutputStreamWriter(outputStream).use { writer -> writer.write("Hello World!") }

                    val changeSet = MetadataChangeSet.Builder()
                            .setTitle("HelloWorld.docx")
                            .setMimeType("text/plain")

                            // .setMimeType("application/vnd.google-apps.document")
                            .setStarred(true)
                            .build()



                    getDriveResourceClient().createFile(parent, changeSet, contents)
                }
                .addOnSuccessListener(this
                ) { driveFile ->
                    Log.e("shohiebsenseee ", driveFile.getDriveId().toString())
                    showMessage(getString(R.string.file_created) + " " + driveFile.getDriveId().encodeToString())
                    //finish()
                }
                .addOnFailureListener(this, { e ->
                    Log.e(TAG, "Unable to create file", e)
                    showMessage(getString(R.string.file_create_error))
                    finish()
                })
        // [END create_file]
    }
}