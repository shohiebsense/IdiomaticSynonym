package com.shohiebsense.idiomaticsynonym.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay.IdiomsSummaryFragment.OnClickedItemListener
import kotlinx.android.synthetic.main.obsolete_item_indexed_sentence.view.*

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnClickedItemListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyIndexedSentenceListRecyclerViewAdapter(private val mValues: ArrayList<IndexedSentence>, private val mListener: OnClickedItemListener?) : RecyclerView.Adapter<MyIndexedSentenceListRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.obsolete_item_indexed_sentence, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.itemView.sentenceNumber.text = mValues[position].index.toString()

        holder.mView.setOnClickListener {
           // mListener?.onListFragmentInteraction(holder.mItem!!)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mItem: IndexedSentence? = null
    }
}
