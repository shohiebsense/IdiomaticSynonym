package com.shohiebsense.idiomaticsynonym.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.activity.detail.fragment.IdiomsSummaryFragment
import kotlinx.android.synthetic.main.item_idioms.view.*


/**
 * Created by Shohiebsense on 04/06/2018
 */

class IdiomSumarryAdapter(val idioms: List<String>, val mListener: IdiomsSummaryFragment.OnClickedItemListener) : RecyclerView.Adapter<IdiomSumarryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_idiom_slide, parent, false)
        return ViewHolder(view)    }

    override fun getItemCount(): Int = idioms.size - 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val idiom = idioms[position]
        holder.itemView.itemTextView.text = idiom
        holder.itemView.setOnClickListener {
            mListener.onItemClicked(idiom)
        }
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

    }
}