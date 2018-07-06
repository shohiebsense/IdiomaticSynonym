package com.shohiebsense.idiomaticsynonym.view.adapter

import android.content.Context
import android.widget.TextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.shohiebsense.idiomaticsynonym.R
import com.snatik.storage.Storage
import java.io.File


/**
 * Created by Shohiebsense on 24/06/2018
 */

class FilesAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mFiles: List<File>? = null
    private var mListener: OnFileItemListener? = null
    private val mStorage: Storage

    init {
        mStorage = Storage(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = mFiles!![position]
        val fileViewHolder = holder as FileViewHolder
        fileViewHolder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mListener!!.onClick(file)
            }
        })
        fileViewHolder.itemView.setOnLongClickListener(object : View.OnLongClickListener{
            override fun onLongClick(view: View): Boolean {
                mListener!!.onLongClick(file)
                return true
            }
        })
        fileViewHolder.mName.setText(file.getName())
        fileViewHolder.mIcon.setImageResource(if (file.isDirectory())
            R.drawable.ic_folder
        else
            R.drawable
                    .ic_file)
        if (file.isDirectory()) {
            fileViewHolder.mSize.visibility = View.GONE
        } else {
            fileViewHolder.mSize.visibility = View.VISIBLE
            fileViewHolder.mSize.setText(mStorage.getReadableSize(file))
        }

    }

    override fun getItemCount(): Int {
        return if (mFiles != null) mFiles!!.size else 0
    }

    fun setFiles(files: List<File>) {
        mFiles = files
    }

    fun setListener(listener: OnFileItemListener) {
        mListener = listener
    }

    internal class FileViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        var mName: TextView
        var mSize: TextView
        var mIcon: ImageView

        init {
            mName = v.findViewById(R.id.name)
            mSize = v.findViewById(R.id.size)
            mIcon = v.findViewById(R.id.icon) as ImageView
        }
    }

    interface OnFileItemListener {
        fun onClick(file: File)

        fun onLongClick(file: File)
    }
}