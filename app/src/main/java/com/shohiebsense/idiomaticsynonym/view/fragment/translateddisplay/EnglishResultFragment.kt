package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.cloud.translate.Translate
import com.klinker.android.link_builder.LinkBuilder
import com.klinker.android.link_builder.TouchableMovementMethod
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.event.*
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_english_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * A simple [Fragment] subclass.
 * Use the [EnglishResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnglishResultFragment : Fragment(), BookmarkDataEmitter.SingleBookmarkCallback {



    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment EnglishResultFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(lastId: Int): EnglishResultFragment {
            val fragment = EnglishResultFragment()
            val args = Bundle()
            args.putInt(TranslatedDisplayActivity.INTENT_LAST_ID, lastId)
            fragment.arguments = args
            return fragment
        }
    }



    // TODO: Rename and change types of parameters
    private var fileName: String? = null
    lateinit var idioms : List<String>
    var lastId = 0
    lateinit var idiomMeaningFastAdapter: FastAdapter<IdiomMeaningItem>
    lateinit var idiomMeaningItemAdapter: ItemAdapter<IdiomMeaningItem>
    lateinit var behaviour : BottomSheetBehavior<View>

    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(word: String) {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
            (activity as TranslatedDisplayActivity).isFromEnglishFragment = true
            (activity as TranslatedDisplayActivity).getSynonym(word)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDisplaySynonyms(event : EnglishFragmentSynonymEvent){
        if(event.synonyms.isEmpty()){
            return;
        }
        var items = mutableListOf<IdiomMeaningItem>()

        AppUtil.makeErrorLog("tapi nyampe sini kannnn "+event.synonyms)

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


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        AppUtil.makeErrorLog("destroyeddd")
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        EventBus.getDefault().post(FragmentEvent())
        if (arguments != null) {
            lastId = arguments!!.getInt(TranslatedDisplayActivity.INTENT_LAST_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_english_result, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUtil.makeErrorLog("aku dulu kan?")
        behaviour = BottomSheetBehavior.from(bottomSheetLayout)



        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        onShowingBottomSheet()
        bottomSheetLayout.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }


        englishTextsTextView.movementMethod = LinkMovementMethod()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGettingBookmark(event : BookmarkViewEvent){
        AppUtil.makeErrorLog("ongettingbookmark")
        englishTextsTextView.text = (activity as TranslatedDisplayActivity).bookmark.english
        idioms = AppUtil.getListOfIdioms((activity as TranslatedDisplayActivity).bookmark.idioms)
        (activity as TranslatedDisplayActivity).isFromEnglishFragment = true
        (activity as TranslatedDisplayActivity).wordClickableService.generateClickableSpan((activity as TranslatedDisplayActivity).bookmark.english.toString(),(activity as TranslatedDisplayActivity).bookmark.idioms,behaviour)
    }



    override fun onFetched(bookmark: BookmarkedEnglish) {
        englishTextsTextView.text = bookmark.english
        AppUtil.makeErrorLog("hello new world "+bookmark.idioms)
        idioms = AppUtil.getListOfIdioms(bookmark.idioms)
        (activity as TranslatedDisplayActivity).isFromEnglishFragment = true
        (activity as TranslatedDisplayActivity).wordClickableService.generateClickableSpan(bookmark.english.toString(),bookmark.idioms,behaviour)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCompleted(linksEvent : EnglishFragmentLinksEvent) {
        AppUtil.makeErrorLog("sampaiiii kawan")

        Observable.just(LinkBuilder.from(activity!!,(activity as TranslatedDisplayActivity).bookmark.english.toString()).addLinks(linksEvent.links).build()
        ).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()) .subscribe {
            englishTextsTextView.setText(it)
            englishTextsTextView.movementMethod = TouchableMovementMethod.instance
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClickedIdiomText(idiomEvent: EnglishFragmentIdiomEvent) {
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
        //}
        /*  else{
              getIndexedSentence()
          }*/

        toggleBottomSheet()

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowingOnlineTranslation(meaningsEvent: EnglishFragmentMeaningsEvent) {
        var items = mutableListOf<IdiomMeaningItem>()
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

    override fun onFailedFetched() {

    }

}// Required empty public constructor
