package com.shohiebsense.idiomaticsynonym.view.activity.createfile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.CreateFileService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.FilesAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.shohiebsense.idiomaticsynonym.view.custom.CreateFileDialogFragment
import kotlinx.android.synthetic.main.activity_create_file.*
import kotlinx.android.synthetic.main.activity_create_file_root.*
import java.io.File
import android.content.Intent
import com.snatik.storage.helpers.SizeUnit
import android.content.ActivityNotFoundException
import android.support.v4.content.FileProvider
import android.webkit.MimeTypeMap
import com.shohiebsense.idiomaticsynonym.R
import com.snatik.storage.helpers.OrderType
import java.util.*

class CreateFileActivity : AppCompatActivity(), BookmarkDataEmitter.SingleBookmarkCallback, FilesAdapter.OnFileItemListener, CreateFileDialogFragment.DialogListener {


    private val TAG = "CreateFileActivity"
    var bookmarkId = 0
    companion object {
        val INTENT_ID = "id"
    }

    private var mTreeSteps = 0
    var bookmarkEmitter : BookmarkDataEmitter? = null
    lateinit var fileName : String
    lateinit var mFilesAdapter : FilesAdapter
    lateinit var createFileService: CreateFileService
    lateinit var bookmark : BookmarkedEnglish


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookmarkId = intent.getIntExtra(INTENT_ID,0)

        setContentView(R.layout.activity_create_file_root)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        createFileService = CreateFileService().builder(this)
        bookmarkEmitter = BookmarkDataEmitter(this)
        bookmarkEmitter!!.getEnglishBookmark(bookmarkId,this)

        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                CreateFileDialogFragment.newInstance().show(fragmentManager,CreateFileDialogFragment::class.simpleName)
            }
        })

    }


    override fun onFetched(bookmark: BookmarkedEnglish) {
        this.bookmark = bookmark
        val layoutManager = LinearLayoutManager(this)
        recycler_file.setLayoutManager(layoutManager)
        mFilesAdapter = FilesAdapter(applicationContext)
        mFilesAdapter.setListener(this)
        recycler_file.setAdapter(mFilesAdapter)
        showFiles(createFileService.storage.getExternalStorageDirectory());
    }

    override fun onFailedFetched() {

    }

    override fun onBackPressed() {
        if (mTreeSteps > 0) {
            val path = getPreviousPath()
            mTreeSteps--
            showFiles(path)
            return
        }
        super.onBackPressed()
    }

    private fun getPreviousPath(): String {
        val path = getCurrentPath()
        val lastIndexOf = path.lastIndexOf(File.separator)
        if (lastIndexOf < 0) {
            return getCurrentPath()
        }
        return path.substring(0, lastIndexOf)
    }

    private fun getCurrentPath(): String {
        return text_path.getText().toString()
    }

    private fun showFiles(path: String) {
        text_path.setText(path)
        val files = createFileService.storage.getFiles(path)
        if (files != null) {
            Collections.sort(files, OrderType.NAME.comparator)
        }
        mFilesAdapter.setFiles(files)
        mFilesAdapter.notifyDataSetChanged()
    }

    override fun onClick(file: File) {
        if (file.isDirectory)
        {
            mTreeSteps++
            val path = file.absolutePath
            showFiles(path)
        }
        else
        {

            try
            {
                val intent = Intent(Intent.ACTION_VIEW)
                val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(AppUtil.fileExt(file.absolutePath))
                val apkURI = FileProvider.getUriForFile(
                        this,packageName + ".provider", file)
                intent.setDataAndType(apkURI, mimeType)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
            catch (e:ActivityNotFoundException) {
                if (createFileService.storage.getSize(file, SizeUnit.KB) > 500)
                {
                    //Helper.showSnackbar("The file is too big for preview", mRecyclerView)
                    return
                }
               // val intent = Intent(this, ViewTextActivity::class.java)
                //intent.putExtra(ViewTextActivity.EXTRA_FILE_NAME, file.name)
                //intent.putExtra(ViewTextActivity.EXTRA_FILE_PATH, file.absolutePath)
                //startActivity(intent)
            }
            catch (e:NullPointerException) {

            }

            }
    }

    override fun onLongClick(file: File) {
    }

    override fun onNewFile(name: String) {
        createFileService.create(getCurrentPath(), name,bookmark.indonesian!!)
    }
}
