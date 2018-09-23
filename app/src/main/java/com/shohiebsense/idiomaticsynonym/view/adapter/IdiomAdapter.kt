package com.shohiebsense.idiomaticsynonym.view.adapter

import android.app.Activity
import android.support.constraint.ConstraintLayout
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.Idiom
import kotlinx.android.synthetic.main.item_idiom.view.*


/**
 * Created by Shohiebsense on 25/11/2017.
 */
class IdiomAdapter(var context: Activity, val idioms: ArrayList<Idiom>) : PagerAdapter() {

    lateinit var inflater : LayoutInflater

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = LayoutInflater.from(context)
        var layout = inflater.inflate(R.layout.item_idiom, container, false)
        var idiom = idioms[position]
        layout.text_title.text = idiom.idiom
        layout.text_translation.text = idiom.translation
        if(idiom.similar != null){
            layout.text_similar.text = idiom.similar
        }
        container.addView(layout)
        return layout
    }


    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view.equals(`object` as ConstraintLayout)
    }

    override fun getCount(): Int = idioms.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}