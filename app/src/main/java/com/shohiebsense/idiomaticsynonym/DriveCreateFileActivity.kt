package com.shohiebsense.idiomaticsynonym

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.drive.DriveFile
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.tasks.Tasks
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import java.io.OutputStreamWriter
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.events.ChangeEvent
import com.google.android.gms.drive.events.OnChangeListener
import com.google.android.gms.drive.events.ListenerToken
import com.google.android.gms.tasks.OnSuccessListener
import com.shohiebsense.idiomaticsynonym.model.event.MyDriveEventService
import kotlinx.android.synthetic.main.activity_splash.*


class DriveCreateFileActivity  : BaseDemoActivity(), BookmarkDataEmitter.SingleBookmarkCallback {


    private val TAG = "CreateFileActivity"
    var bookmarkId = 0
    private var mChangeListenerToken: ListenerToken? = null
    companion object {
        val INTENT_ID = "id"
    }
    var bookmarkEmitter : BookmarkDataEmitter? = null
    lateinit var mBroadcastReceiver: BroadcastReceiver
    lateinit var fileName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookmarkId = intent.getIntExtra(INTENT_ID,0)
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val event = intent.getParcelableExtra<ChangeEvent>("event")
                AppUtil.makeErrorLog("got an eventt "+event.driveId.resourceId)
            }
        }
        setContentView(R.layout.activity_splash)
        loadingDescTextView.setText("uploading ....")
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mBroadcastReceiver, IntentFilter(MyDriveEventService.CHANGE_EVENT))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
        super.onStop()
    }


    override fun onDriveClientReady() {
        bookmarkEmitter = BookmarkDataEmitter(this)
        bookmarkEmitter!!.getEnglishBookmark(bookmarkId,this)
    }



    private fun createFile(id: Int, onlyFileName: String, indonesian: CharSequence?) {
        // [START create_file]
        val rootFolderTask = getDriveResourceClient().getRootFolder()
        val createContentsTask = getDriveResourceClient().createContents()
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask<DriveFile> { task ->
                    val parent = rootFolderTask.getResult()
                    val contents = createContentsTask.getResult()
                    val outputStream = contents.getOutputStream()
                    OutputStreamWriter(outputStream).use { writer -> writer.write(indonesian.toString()) }
                    var fromXprediom = "-fromXprediom"
                    fileName = onlyFileName+fromXprediom+".docx"
                    val changeSet = MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build()
                    getDriveResourceClient().createFile(parent, changeSet, contents)
                }
                .addOnSuccessListener(this
                ) { driveFile ->
                     driveResourceClient.addChangeListener(driveFile,changeListener).addOnSuccessListener(this) { listenertoken -> mChangeListenerToken = listenertoken };
                }
                .addOnFailureListener(this) { e ->
                    Log.e(TAG, "Unable to create file", e)
                    AppUtil.makeErrorLog("hoiii "+e.toString())
                    showMessage(getString(R.string.file_create_error))
                    //()
                }
        // [END create_file]
    }

    private val changeListener = object : OnChangeListener {
        override fun onChange(event: ChangeEvent) {

            AppUtil.makeErrorLog("got a resource idd "+event.driveId.resourceId)
            if(event.driveId.resourceId != null && bookmarkEmitter != null)
            bookmarkEmitter!!.updateUploadId(bookmarkId.toString(),event.driveId.encodeToString())
            val intent = Intent(this@DriveCreateFileActivity,MainActivity::class.java)
            AppUtil.setFileUploadedEvent(this@DriveCreateFileActivity,true)
            AppUtil.setFileUploadedNameEvent(this@DriveCreateFileActivity, fileName)
            startActivity(intent)
            finish()
        }
    }


    override fun onFetched(bookmark: BookmarkedEnglish) {
        createFile(bookmark.id,AppUtil.getOnlyFileName(bookmark.fileName),bookmark.indonesian)
    }

    override fun onFailedFetched() {
    }

    override fun onDestroy() {
        getDriveResourceClient().removeChangeListener(mChangeListenerToken!!);
        super.onDestroy()

    }
}