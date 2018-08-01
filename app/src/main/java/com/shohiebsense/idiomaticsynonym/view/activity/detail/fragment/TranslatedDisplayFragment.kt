package com.shohiebsense.idiomaticsynonym.view.activity.detail.fragment

import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.Html
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.activity.detail.DetailActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.api.ChosenSynonymWord
import com.shohiebsense.idiomaticsynonym.model.event.BookmarkViewEvent
import com.shohiebsense.idiomaticsynonym.model.event.UpdatedTranslationEvent
import com.shohiebsense.idiomaticsynonym.model.event.ViewEvent
import com.shohiebsense.idiomaticsynonym.services.TranslatedDisplayService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.kateglo.KategloService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.TranslatedDisplayCallback
import com.shohiebsense.idiomaticsynonym.view.items.KategloItem
import com.shohiebsense.idiomaticsynonym.view.items.KategloViewHolder
import com.spyhunter99.supertooltips.ToolTip
import com.spyhunter99.supertooltips.ToolTipManager
import kotlinx.android.synthetic.main.fragment_translated_display.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
class TranslatedDisplayFragment : Fragment(), KategloViewHolder.KategloItemListener, TranslatedDisplayCallback, KategloService.KategloListener, BookmarkDataEmitter.SingleBookmarkCallback {


    lateinit var translatedTextList: ArrayList<String>
    //lateinit var idiomsList : HashMap<Int,String>
    //dev only
    lateinit var idiomsList : MutableList<String>
    lateinit var translatedDisplayService : TranslatedDisplayService
    var kategloNewService = KategloService()
    lateinit var fastAdapter : FastAdapter<KategloItem>
    lateinit var itemAdapter : ItemAdapter<KategloItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var indices : ArrayList<Int>
    lateinit var translatedSpannable : SpannableString
    var tooltips: ToolTipManager? = null
    var lastId = 0

    companion object {
        fun newInstance(lastId: Int) : TranslatedDisplayFragment {
            val args = Bundle()
            args.putInt(DetailActivity.INTENT_LAST_ID, lastId)
            val fragment = TranslatedDisplayFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        lastId = arguments!!.getInt(DetailActivity.INTENT_LAST_ID)
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
        translatedDisplayService = TranslatedDisplayService(context!!, this)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdatedTranslation(event : UpdatedTranslationEvent) {
        AppUtil.makeErrorLog("translation updated?")
        translatedTextView.text = Html.fromHtml((activity as DetailActivity).bookmark.indonesian.toString())
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeOrientation(e: ViewEvent) {
        AppUtil.makeErrorLog("on orientation changed")
        if(!e.isWrapped){
            val newlinesentence = AppUtil.separateParagraphIntoEachLine((activity as DetailActivity).bookmark.indonesian.toString(),(activity as DetailActivity).bookmark.indexedSentences)
            translatedTextView.setText(Html.fromHtml(newlinesentence))
        }
        else{
            translatedTextView.setText(Html.fromHtml((activity as DetailActivity).bookmark.indonesian.toString()))
        }
    }


    override fun onFinishExtractText(decoratedSpan: CharSequence) {

       /* decoratedSpan.forEachIndexed{
            index, sentence ->
            if(indices.contains(index)){
                //styling
            }
            translatedTextView.append(decoratedSpan)
        }*/
        translatedTextView.setText(decoratedSpan)
    }

    override fun onGetSynonyms(syonyms: MutableList<String>) {
        var items = mutableListOf<KategloItem>()

        AppUtil.makeDebugLog("komplittt ")
        itemAdapter.clear()
        for (synonym in syonyms) {
            items.add(KategloItem().withText(ChosenSynonymWord(synonym, index),this@TranslatedDisplayFragment))

        }
        itemAdapter.add(items)
        fastAdapter.notifyAdapterDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGettingBookmark(event : BookmarkViewEvent){
        translatedTextView.text = Html.fromHtml((activity as DetailActivity).bookmark.indonesian.toString())
    }

    override fun onFetched(bookmark: BookmarkedEnglish) {
        //this.bookmark = bookmark
        AppUtil.makeErrorLog("bukannya adaan "+bookmark.uploadId)
        val newlinesentence = AppUtil.separateParagraphIntoEachLine(bookmark.indonesian.toString(),bookmark.indexedSentences)
        translatedTextView.setText(Html.fromHtml(newlinesentence))
    }

    override fun onFailedFetched() {

    }


    override fun onResume() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        super.onResume()
    }

    override fun onDestroy() {
        AppUtil.makeErrorLog("destroyeddd")
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}