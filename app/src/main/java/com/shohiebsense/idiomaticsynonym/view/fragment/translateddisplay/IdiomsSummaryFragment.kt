package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.klinker.android.link_builder.TouchableMovementMethod
import com.leochuan.ScaleLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.model.event.*
import com.shohiebsense.idiomaticsynonym.services.WordClickableService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.IdiomAdapter
import com.shohiebsense.idiomaticsynonym.view.adapter.IdiomCardAdapter
import com.shohiebsense.idiomaticsynonym.view.adapter.MyIndexedSentenceListRecyclerViewAdapter
import com.shohiebsense.idiomaticsynonym.view.callbacks.WordClickableCallback
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import com.shohiebsense.idiomaticsynonym.view.items.IdiomViewHolder
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_idioms_summary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class IdiomsSummaryFragment : Fragment(), BookmarkDataEmitter.IndexedSentenceCallback, BookmarkDataEmitter.SingleBookmarkCallback {



    // TODO: Customize parameters
    lateinit var idioms: List<String>
    lateinit var layoutManager : ScaleLayoutManager
    lateinit var cardLayoutManager : RecyclerView.LayoutManager
    lateinit var idiomMeaningFastAdapter: FastAdapter<IdiomMeaningItem>
    lateinit var idiomMeaningItemAdapter: ItemAdapter<IdiomMeaningItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var bookmarkDataEmitter : BookmarkDataEmitter

    var lastId = 0
    private var mListener: OnClickedItemListener = object : OnClickedItemListener {
        override fun onItemClicked(item: String) {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
            (activity as TranslatedDisplayActivity).isFromEnglishFragment = false
            (activity as TranslatedDisplayActivity).wordClickableService.getMeaningFromIdiom(item)
        }
    }

    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(word: String) {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
            (activity as TranslatedDisplayActivity).getSynonym(word)
        }
    }

    @Subscribe
    fun onDisplaySynonyms(event : IdiomsSummarySynonymEvent){
        if(event.synonyms.isEmpty()){
            return;
        }
        var items = mutableListOf<IdiomMeaningItem>()

        for(synonym in event.synonyms){
            var synonymItem = IdiomMeaningItem().withIdiomMeaning(synonym,idiomItemClickedListener)
            items.add(synonymItem)
        }
        // selectedIdiomList!!.put(index,idiomList.first())
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
        idiomMeaningItemAdapter.clear()
        idiomMeaningItemAdapter.add(items)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        toggleBottomSheet()
    }


    companion object {
        // TODO: Customize parameter initialization
        fun newInstance(lastId : Int): IdiomsSummaryFragment {
            val fragment = IdiomsSummaryFragment()
            val args = Bundle()
            args.putInt(TranslatedDisplayActivity.INTENT_LAST_ID, lastId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeOrientation(e: IdiomCardViewEvent) {
        if(!e.isSlideShow){
            idiomsCardRecyclerView.visibility = View.VISIBLE
            idiomsRecyclerView.visibility = View.GONE
        }
        else{
            idiomsCardRecyclerView.visibility = View.GONE
            idiomsRecyclerView.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastId = arguments!!.getInt(TranslatedDisplayActivity.INTENT_LAST_ID)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_idioms_summary, container, false)


        // Set the adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        layoutManager = ScaleLayoutManager.Builder(activity,AppUtil.Dp2px(activity, 5f))
                .setMinScale(0.95f)
                .build()
        cardLayoutManager = SpannedGridLayoutManager(
                orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                spans = 3)
        bookmarkDataEmitter = BookmarkDataEmitter(activity!!)
        bookmarkDataEmitter.getEnglishBookmark(lastId,this)

        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        onShowingBottomSheet()
    }


    override fun onFetched(bookmark: BookmarkedEnglish) {
        if(bookmark.idioms.isEmpty()){
            emptyTextView.text = "Empty"
        }
        idioms = AppUtil.getListOfIdioms(bookmark.idioms)
        //CenterSnapHelper().attachToRecyclerView(idiomsRecyclerView)
        val adapter = IdiomAdapter(idioms,mListener)
        val cardAdapter = IdiomCardAdapter(idioms,mListener)
        idiomsRecyclerView.layoutManager = layoutManager
        idiomsRecyclerView.adapter = adapter
        /* idiomsCardRecyclerView.layoutManager = SpannedGridLayoutManager(
                 orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
                 spans = 3)*/
        idiomsCardRecyclerView.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
        idiomsCardRecyclerView.adapter = cardAdapter
        AppUtil.makeErrorLog("idioms sizee "+idioms.size)
    }


    override fun onFetched(indexedSentences: ArrayList<IndexedSentence>) {
        Completable.create {
            idiomsRecyclerView.layoutManager = LinearLayoutManager(context)
            idiomsRecyclerView.adapter = MyIndexedSentenceListRecyclerViewAdapter(indexedSentences, mListener)
            if(indexedSentences.isEmpty()){
                emptyTextView.text = "Empty"
            }
            it.onComplete()
        }.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }


    fun toggleBottomSheet(){
        if(behaviour.state != BottomSheetBehavior.STATE_EXPANDED){
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else{
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
        //isShowingIdiom = !isShowingIdiom
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
    fun onClickedIdiomText(idiomEvent : IdiomsSummarrydiomEvent) {
        if(idiomEvent.idiom.isBlank()){
            return;
        }
        var items = mutableListOf<IdiomMeaningItem>()
        if(idiomEvent.idiom.contains(",")){
            val regex = ","
            var idiomList = idiomEvent.idiom.split(regex).toMutableList()

            for(meanings in idiomList){
                AppUtil.makeDebugLog("many choices : "+meanings)
                var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(meanings,idiomItemClickedListener)
                items.add(idiomMeaningItem)
            }
            // selectedIdiomList!!.put(index,idiomList.first())
        }
        else{

            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(idiomEvent.idiom,idiomItemClickedListener)
            items.add(idiomMeaningItem)
        }

        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
        idiomMeaningItemAdapter.clear()
        idiomMeaningItemAdapter.add(items)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        toggleBottomSheet()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowingOnlineTranslation(meaningsEvent : IdiomsSummaryMeaningsEvent) {
        var items = mutableListOf<IdiomMeaningItem>()
        AppUtil.makeErrorLog("meanings???? "+meaningsEvent.meanings?.size)
        meaningsEvent.meanings?.forEach {
            it->
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(it,idiomItemClickedListener)
            items.add(idiomMeaningItem)
        }
        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    idiomMeaningItemAdapter.clear()
                    idiomMeaningItemAdapter.add(items)
                    idiomRecyclerView.adapter = idiomMeaningFastAdapter

                    toggleBottomSheet()
                }
            }
        }.start()
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnClickedItemListener {
        // TODO: Update argument type and name
        fun onItemClicked(item: String)
    }


}
