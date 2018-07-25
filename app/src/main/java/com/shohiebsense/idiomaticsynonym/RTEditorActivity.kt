/*
 * Copyright (C) 2015-2018 Emanuel Moecklin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.shohiebsense.idiomaticsynonym

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.onegravity.rteditor.RTManager
import com.onegravity.rteditor.api.RTApi
import com.onegravity.rteditor.api.RTMediaFactoryImpl
import com.onegravity.rteditor.api.RTProxyImpl
import com.onegravity.rteditor.api.format.RTFormat
import com.onegravity.rteditor.media.MediaUtils
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.utils.FileHelper
import de.mateware.snacky.Snacky
import kotlinx.android.synthetic.main.activity_editor.*
import kotlinx.android.synthetic.main.view_editor_content.*

import java.io.File

class RTEditorActivity : RTEditorBaseActivity(), BookmarkDataEmitter.SingleBookmarkCallback, BookmarkDataEmitter.UpdateBookmarkCallback {

    private var mRTManager: RTManager? = null

    private var mUseDarkTheme: Boolean = false
    private var mSplitToolbar: Boolean = false

    var id = 0
    lateinit var bookmarkDataEmitter : BookmarkDataEmitter

    override fun onCreate(savedInstanceState: Bundle?) {
        // read parameters
        var message: String? = null
        if (savedInstanceState == null) {
            val intent = intent
            message = getStringExtra(intent, RTEditorBaseActivity.PARAM_MESSAGE)
            mUseDarkTheme = intent.getBooleanExtra(RTEditorBaseActivity.PARAM_DARK_THEME, false)
            mSplitToolbar = intent.getBooleanExtra(RTEditorBaseActivity.PARAM_SPLIT_TOOLBAR, false)

        } else {
            mUseDarkTheme = savedInstanceState.getBoolean(RTEditorBaseActivity.PARAM_DARK_THEME, false)
            mSplitToolbar = savedInstanceState.getBoolean(RTEditorBaseActivity.PARAM_SPLIT_TOOLBAR, false)
        }

        // set theme

        super.onCreate(savedInstanceState)

        // set layout
        setContentView(R.layout.activity_editor)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.title = getString(R.string.update_translation)


        id = intent.getIntExtra(INTENT_ID,0)
        bookmarkDataEmitter = BookmarkDataEmitter(this)


        // initialize rich text manager
        val rtApi = RTApi(this, RTProxyImpl(this), RTMediaFactoryImpl(this, true))
        mRTManager = RTManager(rtApi, savedInstanceState)

        // register toolbar 0 (if it exists)


        // register toolbar 1 (if it exists)

        // register toolbar 2 (if it exists)


        // register message editor
        mRTManager!!.registerEditor(rtEditText_1!!, true)
        if (message != null) {
            rtEditText_1!!.setRichTextEditing(true, message)
        }

        bookmarkDataEmitter.getEnglishBookmark(id,this)


        // register signature editor



        rtEditText_1!!.requestFocus()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (mRTManager != null) {
            mRTManager!!.onDestroy(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mRTManager!!.onSaveInstanceState(outState)


        outState.putBoolean(RTEditorBaseActivity.PARAM_DARK_THEME, mUseDarkTheme)
        outState.putBoolean(RTEditorBaseActivity.PARAM_SPLIT_TOOLBAR, mSplitToolbar)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null && data.data!!.path != null) {
            var filePath = data.data!!.path

            if (requestCode == REQUEST_SAVE_FILE) {
                /*
                 * Save file.
                 *
                 * Of course this is a hack but since this is just a demo
                 * to show how to integrate the rich text editor this is ok ;-)
                 */

                // write subject
                var targetFile = MediaUtils.createUniqueFile(File(filePath), "subject.html", true)

                // write message
                targetFile = File(targetFile.absolutePath.replace("subject_", "message_"))
                FileHelper.save(this, targetFile, rtEditText_1!!.getText(RTFormat.HTML))

                // write signature
                targetFile = File(targetFile.absolutePath.replace("message_", "signature_"))


            } else if (requestCode == REQUEST_LOAD_FILE) {
                /*
                 * Load File
                 *
                 * A hack, I know ...
                 */

                if (filePath.contains("message_")) {
                    filePath = filePath.replace("message_", "subject_")
                } else if (filePath.contains("signature_")) {
                    filePath = filePath.replace("signature_", "subject_")
                }

                if (filePath.contains("subject_")) {
                    // load subject
                    var s = FileHelper.load(this, filePath)

                    // load message
                    filePath = filePath.replace("subject_", "message_")
                    s = FileHelper.load(this, filePath)
                    rtEditText_1!!.setRichTextEditing(true, s)

                    // load signature
                    filePath = filePath.replace("message_", "signature_")
                    s = FileHelper.load(this, filePath)
                } else {
                    Toast.makeText(this, R.string.load_failure_1, Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onFetched(bookmark: BookmarkedEnglish) {

        rtEditText_1!!.setRichTextEditing(true, bookmark.indonesian.toString())

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_update_indonesian, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.updateIndonesianOption -> {
                bookmarkDataEmitter.updateTranslation(rtEditText_1.getText(RTFormat.SPANNED),id.toString(),this)
            }


        }
        return super.onOptionsItemSelected(item)
    }


    override fun onError(message: String) {
        AppUtil.showSnackbar(this,AppUtil.SNACKY_ERROR,getString(R.string.failed_update))
    }

    override fun onSuccessUpdatingTranslation() {
        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun onFailedFetched() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED,intent)
        finish()
    }

    private fun startAndFinish(clazz: Class<out Activity>) {
        val message = rtEditText_1.getText(RTFormat.HTML)
        val intent = Intent(this, clazz)
                .putExtra(RTEditorBaseActivity.PARAM_DARK_THEME, mUseDarkTheme)
                .putExtra(RTEditorBaseActivity.PARAM_SPLIT_TOOLBAR, mSplitToolbar)
                .putExtra(RTEditorBaseActivity.PARAM_MESSAGE, message)
        startActivity(intent)
        finish()
    }

    companion object {
        val INTENT_ID = "englishid"

        private val REQUEST_LOAD_FILE = 1
        private val REQUEST_SAVE_FILE = 2
    }

}
