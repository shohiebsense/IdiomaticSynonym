package com.shohiebsense.idiomaticsynonym

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_update_indonesian.*

class UpdateIndonesianActivity : AppCompatActivity(), BookmarkDataEmitter.SingleBookmarkCallback, BookmarkDataEmitter.UpdateBookmarkCallback {



    var id = 0
    lateinit var bookmarkDataEmitter : BookmarkDataEmitter

    companion object {
        val INTENT_ID = "englishid"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getIntExtra(INTENT_ID,0)
        bookmarkDataEmitter = BookmarkDataEmitter(this)
        bookmarkDataEmitter.getEnglishBookmark(id,this)
        setContentView(R.layout.activity_update_indonesian)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_update_indonesian, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.updateIndonesianOption -> {
                bookmarkDataEmitter.updateIndonesianText(indonesianEditText.text.toString(),id.toString(),this)
            }


        }
        return super.onOptionsItemSelected(item)
    }



    override fun onFetched(bookmark: BookmarkedEnglish) {
        indonesianEditText.setText(bookmark.indonesian)
    }

    override fun onError() {
        Toasty.success(this,getString(R.string.failed_update)).show()
    }

    override fun onSuccess() {
        Toasty.success(this,getString(R.string.success_update)).show()
        val intent = Intent()
        setResult(Activity.RESULT_OK,intent)
        finish()
    }
}
