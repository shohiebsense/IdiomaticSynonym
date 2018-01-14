package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.api.ChosenSynonymWord
import com.shohiebsense.idiomaticsynonym.services.TranslatedDisplayService
import com.shohiebsense.idiomaticsynonym.services.kateglo.KategloServiceNe
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.TranslatedDisplayCallback
import com.shohiebsense.idiomaticsynonym.view.items.KategloItem
import com.shohiebsense.idiomaticsynonym.view.items.KategloViewHolder
import com.spyhunter99.supertooltips.ToolTip
import com.spyhunter99.supertooltips.ToolTipManager
import kotlinx.android.synthetic.main.fragment_translated_display.*

/**
 * Created by Shohiebsense on 11/12/2017.
 *
 * terusinnnnnnnnnn, sinonim dari kata yang dipilih
 *
 *
 * done
 *
 * cek kata idiom yang diterjemahin, kok karena. harusny abukan karena
 */
class TranslatedDisplayFragment : Fragment(), KategloViewHolder.KategloItemListener, TranslatedDisplayCallback, KategloServiceNe.KategloListener {


    lateinit var translatedTextList: ArrayList<String>
    //lateinit var idiomsList : HashMap<Int,String>
    //dev only
    lateinit var idiomsList : MutableList<String>
    lateinit var translatedDisplayService : TranslatedDisplayService
    var kategloNewService = KategloServiceNe()
    lateinit var fastAdapter : FastAdapter<KategloItem>
    lateinit var itemAdapter : ItemAdapter<KategloItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var indices : ArrayList<Int>
    lateinit var translatedSpannable : SpannableString
    var tooltips: ToolTipManager? = null

    companion object {
        fun newInstance(name: ArrayList<String>, indices: ArrayList<Int>) : TranslatedDisplayFragment {
            val args = Bundle()
            args.putStringArrayList(TranslatedDisplayActivity.INTENT_TRANSLATED_TEXT, name)
            args.putIntegerArrayList(TranslatedDisplayActivity.INTENT_INDICES, indices)
            val fragment = TranslatedDisplayFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //translatedTextList = arguments.getStringArrayList(MainActivity.INTENT_TRANSLATED_TEXT)
       // AppUtil.makeDebugLog("translatedTextList SIZEE "+translatedTextList.size)
        //idiomsList = arguments.getSerializable(MainActivity.INTENT_IDIOM_LIST) as HashMap<Int, String>
       // translatedSpannable = SpannableString("")

        //DEVELOPMENT ONLY
        translatedTextList = arrayListOf<String>()
        translatedTextList.add(AppUtil.translateExample)
        idiomsList = AppUtil.translatedIdioms.split(",").toMutableList()
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.fragment_translated_display, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        tooltips = ToolTipManager(activity)

        kategloItemRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        kategloItemRecyclerView.adapter = fastAdapter
        onShowingBottomSheet()

        //development only
        //  translatedDisplayService = TranslatedDisplayService(context, translatedTextList, idiomsList, this)
        translatedDisplayService = TranslatedDisplayService(context!!, translatedTextList, idiomsList,indices, this)
        translatedDisplayService.extract()

        var spannable :CharSequence="adf aew"
        var threetimes = 0
/*
        for(sentence in translatedTextList){
           spannable = TextUtils.concat(spannable,sentence)
        }



        AppUtil.makeDebugLog("this is "+spannable)
        translatedTextView.text = ""+spannable*/

        //translatedTextView.text = "pada hari minggu ku turut ayah ke kotaaa "+getString(R.string.dialog_message_find_idioms)
    }


    fun onShowingBottomSheet(){
        behaviour = BottomSheetBehavior.from(bottomSheetLayout)
        behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                // React to state change
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
                // React to dragging events
            }
        })
    }

    fun fetchKateglo(meanings: String, index: Int){
       // var kategloSynonyms = mutableListOf<SynonymWord>()
        AppUtil.makeDebugLog("lihatt "+meanings.trim())
      /*  kategloService.getThesaurus("json",meanings.trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableObserver<Thesaurus>() {

                    override fun onComplete() {

                })*/
        kategloNewService.getSynonymStrings(meanings,this)


    }



    override fun onKategloItemClick(text: CharSequence, index: Int) {
        translatedSpannable.replaceRange(index, text.length, text)
    }

    var index = 0
    override fun onSingleSynonymIdiomClicked(idiom: String, index: Int) {
        var toolTip = ToolTip()
                .withText("aai  aewfiaewj  wea")
                .withColor(ContextCompat.getColor(context!!,R.color.slightlyYellowColor))
                .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                .withShadow();
        tooltips!!.showToolTip(toolTip, toolTipsView);
        this.index = index
        fetchKateglo(idiom, index)
        kategloItemRecyclerView.layoutManager = GridLayoutManager(activity,2)
        kategloItemRecyclerView.adapter = fastAdapter


        AppUtil.makeDebugLog("clickeeddd "+idiom)

        if(behaviour.state != BottomSheetBehavior.STATE_EXPANDED){
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else{
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN

        }
    }

    override fun onFinishExtractText(decoratedSpan: CharSequence) {
        translatedTextView.movementMethod = LinkMovementMethod.getInstance()

        decoratedSpan.forEachIndexed{
            index, sentence ->
            if(indices.contains(index)){
                //styling
            }
            translatedTextView.append(decoratedSpan)
        }
        AppUtil.makeDebugLog("finished extract, in TranslatedDisplay "+translatedTextView.text)

    }

    override fun onGetSyonyms(syonyms: MutableList<String>) {
        var items = mutableListOf<KategloItem>()

        AppUtil.makeDebugLog("komplittt ")
        itemAdapter.clear()
        for (synonym in syonyms) {
            items.add(KategloItem().withText(ChosenSynonymWord(synonym, index),this@TranslatedDisplayFragment))

        }
        itemAdapter.add(items)
        fastAdapter.notifyAdapterDataSetChanged()
    }



}