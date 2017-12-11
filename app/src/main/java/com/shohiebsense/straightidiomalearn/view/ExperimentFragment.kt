package com.shohiebsense.straightidiomalearn.view

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.GridLayoutManager
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.view.items.IdiomMeaningItem
import kotlinx.android.synthetic.main.fragment_experiment.*
import org.jetbrains.anko.act

/**
 * Created by Shohiebsense on 26/11/2017.
 */

class ExperimentFragment : Fragment(){

    lateinit var fastAdapter : FastAdapter<IdiomMeaningItem>
    lateinit var itemAdapter : ItemAdapter<IdiomMeaningItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_experiment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        idiomMeaningRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        idiomMeaningRecyclerView.adapter = fastAdapter
        var items = mutableListOf<IdiomMeaningItem>()
        for(i in 0 .. 10){
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(R.string.app_name)
            idiomMeaningItem.withOnItemClickListener { v, adapter, item, position ->
                Toast.makeText(activity, "Hiiii nomr "+i, Toast.LENGTH_SHORT).show()
                true
            }
            items.add(idiomMeaningItem)
        }

        AppUtil.makeDebugLog("sizenya "+items.size)
        itemAdapter.add(items)
        onShowingBottomSheet()
    }

    fun onShowingBottomSheet(){

        var behaviour = BottomSheetBehavior.from(bottomSheetLayout)
        behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                // React to state change
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })


        var spannable  = SpannableString(" ewauhauewhfuaefhw  ewafuha wue ewafuhaweu iefwa")
        var clickableSpan = object : ClickableSpan()
        {
            override fun onClick(widget: View?) {
                //
                Toast.makeText(act, "faoshofewa  oewowe a",Toast.LENGTH_SHORT).show()
                if(behaviour.state != BottomSheetBehavior.STATE_EXPANDED){
                    behaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }
                else{
                    behaviour.state = BottomSheetBehavior.STATE_HIDDEN

                }
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }

        }
        clickableSpan.underlying
        spannable.setSpan(clickableSpan, 8, 15 , Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        textFetchedTextView.text = spannable
        textFetchedTextView.isClickable = true
        textFetchedTextView.movementMethod = LinkMovementMethod.getInstance()

/*
        textFetchedTextView.setOnClickListener {
            if(behaviour.state != BottomSheetBehavior.STATE_EXPANDED){
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
            else{
                behaviour.state = BottomSheetBehavior.STATE_HIDDEN

            }
        }*/
    }

}