package com.shohiebsense.idiomaticsynonym.services

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import com.shohiebsense.idiomaticsynonym.MainActivity
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.snatik.storage.Storage
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File


/**
 * Created by Shohiebsense on 24/06/2018
 */

class CreateFileService {
    lateinit var storage: Storage
    var path = ""
    lateinit var fileName : String
    lateinit var context : Activity

    fun builder(context: Activity) : CreateFileService{
        this.context = context
        storage = Storage(context)
        return this
    }



    fun create(currentPath : String, onlyFileName : String, indonesian: CharSequence){
        path =  currentPath
        fileName = onlyFileName+".txt"
        val newFile = path + File.separator + fileName
        Observable.just(storage.createFile(newFile,indonesian.toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<Boolean>{
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: Boolean) {
                        if(t){
                            MediaScannerConnection.scanFile(context, arrayOf(newFile), null, null);
                            val intent = Intent(context, MainActivity::class.java)
                            AppUtil.setFileUploadedEvent(context,true)
                            AppUtil.setFileUploadedNameEvent(context, fileName)
                            context.startActivity(intent)
                            context.finish()
                        }
                    }

                    override fun onError(e: Throwable) {
                        context.finish()
                    }
                })
    }



}