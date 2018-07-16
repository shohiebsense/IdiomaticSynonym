package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay


import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.view.*
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.hanks.htextview.base.AnimationListener
import com.hanks.htextview.base.HTextView
import com.klinker.android.link_builder.LinkBuilder
import com.klinker.android.link_builder.TouchableMovementMethod
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.model.ReplacedSentence
import com.shohiebsense.idiomaticsynonym.model.event.*
import com.shohiebsense.idiomaticsynonym.services.ReplaceService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.ReplacedHistoryEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import com.shohiebsense.idiomaticsynonym.view.items.SentenceItem
import com.shohiebsense.idiomaticsynonym.view.items.SentenceViewHolder
import com.transitionseverywhere.TransitionManager
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_translated_display.*
import kotlinx.android.synthetic.main.fragment_english_result.*
import kotlinx.android.synthetic.main.fragment_english_result_root.*
import kotlinx.android.synthetic.main.view_popup_translating.*
import kotlinx.android.synthetic.main.view_popup_translating.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Use the [EnglishResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnglishResultFragment : Fragment(), BookmarkDataEmitter.SingleBookmarkCallback, ReplaceService.ReplaceListener, NavigationView.OnNavigationItemSelectedListener, SentenceViewHolder.SentenceItemClickListener {


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

        @JvmStatic
        fun method(strs: Array<String>) {
            for (s in strs)
                println(s)
        }


    }



    // TODO: Rename and change types of parameters
    private var fileName: String? = null
    lateinit var idioms : List<String>
    var lastId = 0
    lateinit var replaceService: ReplaceService
    lateinit var idiomMeaningFastAdapter: FastAdapter<IdiomMeaningItem>
    lateinit var idiomMeaningItemAdapter: ItemAdapter<IdiomMeaningItem>
    lateinit var sentenceFastAdapter : FastAdapter<SentenceItem>
    lateinit var sentenceItemAdapter : ItemAdapter<SentenceItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var replacedHistoryEmitter: ReplacedHistoryEmitter
    var currentClickedIdiom : String = ""
    var isFirstTimeClickedIdiom = true
    var oldSentence = ""
    var newSentence = ""
    var mPopupWindow : PopupWindow? = null

    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(word: String) {
            if(isFirstTimeClickedIdiom){
                currentClickedIdiom = word
                isFirstTimeClickedIdiom = false
            }
            AppUtil.makeErrorLog("current clicked idiom  "+currentClickedIdiom)
            if((activity as TranslatedDisplayActivity).isIdiomSynonymMode){
                behaviour.state = BottomSheetBehavior.STATE_HIDDEN
                (activity as TranslatedDisplayActivity).isFromEnglishFragment = true
                (activity as TranslatedDisplayActivity).getSynonym(word)
            }
            else{
                AppUtil.makeErrorLog("engga ke mode ini kahh?")
                replaceService.existingIdiom = currentClickedIdiom
                replaceService.newIdiom = word
                replaceService.isIdiomTranslationExist((activity as TranslatedDisplayActivity).bookmark.indonesian.toString())

            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onReplacedSentencesExist(foundedSentences: ArrayList<ReplacedSentence>) {
        var items = arrayListOf<SentenceItem>()
        for(i in 0 until foundedSentences.size){
            items.add(SentenceItem().withSentence(foundedSentences[i],this))
        }
        AppUtil.makeErrorLog("new idiom  "+replaceService.newIdiom)
        sentenceItemAdapter.clear()
        sentenceItemAdapter.add(items)
        val layoutManager = LinearLayoutManager(activity)
        recycler_sentences.layoutManager = layoutManager
        recycler_sentences.adapter = sentenceFastAdapter
        val divider = DividerItemDecoration(activity,layoutManager.orientation)
        recycler_sentences.addItemDecoration(divider)
        drawer_layout.openDrawer(GravityCompat.START)
    }

    override fun onSentenceClick(sentence: ReplacedSentence) {
        oldSentence = sentence.sentence
        drawer_layout.closeDrawer(GravityCompat.START)
        replaceService.replaceIdiomInSentence((activity as TranslatedDisplayActivity).bookmark.indonesian.toString(),sentence,replaceService.newIdiom)
    }


    override fun onGettingNewSentence(newSentence: String) {
        this.newSentence = newSentence
    }


    override fun onGettingReplacedTranslation(translation: String) {
        currentClickedIdiom = translation
        replaceService.existingIdiom = currentClickedIdiom
        AppUtil.makeErrorLog("dapatnya apa harusnya udaa "+currentClickedIdiom)
        replaceService.isIdiomTranslationExist((activity as TranslatedDisplayActivity).bookmark.indonesian.toString())
    }

    override fun onGettingOriginalTranslation(translation: String) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdatedTranslation(event : UpdatedTranslationEvent){
        AppUtil.makeErrorLog("engga ke sini ta?")
        if(event.isSucceed){
            var index = 0
            TransitionManager.beginDelayedTransition(layout_constraint);
            topBlurView.visibility = View.VISIBLE
            val texts = arrayOf(oldSentence,newSentence)
            fadeTextView.animateText(oldSentence)
            fadeTextView.setAnimationListener (object : AnimationListener{
                override fun onAnimationEnd(hTextView: HTextView?) {
                    index++
                    if(index == 1){
                        getDelayedExecution(3).subscribe{
                            fadeTextView.animateText(texts[index])
                        }
                    }
                    if(index > 1){
                        getDelayedExecution(3).subscribe(object : CompletableObserver{
                            override fun onComplete() {
                                getDelayedExecution(1).subscribe{
                                    (activity as TranslatedDisplayActivity).refresh()
                                }
                            }
                            override fun onSubscribe(d: Disposable) {
                                TransitionManager.beginDelayedTransition(layout_constraint)
                                topBlurView.visibility = View.GONE
                            }
                            override fun onError(e: Throwable) {
                            }
                        })

                    }

                }
            })
        }
    }

    fun getDelayedExecution(second : Long) : Completable{
        return Completable.timer(second, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
    }

    override fun onAttemptToReplace(translation: String) {
        (activity as TranslatedDisplayActivity).updateTranslation(translation)
    }

    override fun onEmpty() {
        replaceService.replacedHistoryEmitter.getReplacedTranslation((activity as TranslatedDisplayActivity).bookmark.id,replaceService.originIdiom)
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
        if(mPopupWindow != null){
            TransitionManager.beginDelayedTransition(rootCoordinatorLayout)
            mPopupWindow?.dismiss()
        }
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
        return inflater.inflate(R.layout.fragment_english_result_root, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AppUtil.makeErrorLog("aku dulu kan?")
        behaviour = BottomSheetBehavior.from(bottomSheetLayout)
        replaceService = ReplaceService(activity as TranslatedDisplayActivity, this@EnglishResultFragment)

        initBlurView()


        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        sentenceItemAdapter = ItemAdapter.items()
        sentenceFastAdapter = FastAdapter.with(sentenceItemAdapter)
        recycler_sentences.layoutManager = LinearLayoutManager(activity)
        recycler_sentences.adapter = sentenceFastAdapter
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        onShowingBottomSheet()
        bottomSheetLayout.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }

        englishTextsTextView.movementMethod = LinkMovementMethod()
        val toggle = ActionBarDrawerToggle(
                activity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    fun initBlurView(){
        var radius = 25f;
        var minBlurRadius = 10f;
        var step = 4f;

        //set background, if your root layout doesn't have one
        var  windowBackground = activity?.window!!.getDecorView().getBackground();


        topBlurView.setupWith(rootCoordinatorLayout)
                .windowBackground(windowBackground)
                .blurRadius(radius)
                .setHasFixedTransformationMatrix(true)

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
        isFirstTimeClickedIdiom = true
        replaceService.originIdiom = idiomEvent.idiom
        showPopUpWindow()
        AppUtil.makeErrorLog("heyy hdoo")
        if(!(activity as TranslatedDisplayActivity).isIdiomSynonymMode){
            (activity as TranslatedDisplayActivity).getTranslation(idiomEvent.idiom)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowingIdiomTranslation(idiomTranslationEvent: IdiomTranslationEvent){
        currentClickedIdiom = idiomTranslationEvent.translation
        AppUtil.makeErrorLog("haiii cantikk "+currentClickedIdiom)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowingTranslation(meaningsEvent: EnglishFragmentMeaningsEvent) {
        var items = mutableListOf<IdiomMeaningItem>()
        if(isFirstTimeClickedIdiom){
            replaceService.existingIdiom = meaningsEvent.meanings!![0]
        }
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
                    if(mPopupWindow != null){
                        mPopupWindow?.dismiss()
                    }
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

    fun showPopUpWindow(){
         var inflater = activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

                // Inflate the custom layout/view
        var customView = inflater.inflate(R.layout.view_popup_translating,null)
        if(mPopupWindow != null){
            TransitionManager.beginDelayedTransition(rootCoordinatorLayout)
            mPopupWindow?.dismiss()
        }
        mPopupWindow = PopupWindow(
                customView,
       350,150
        );

        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow?.setElevation(5.0f);
        }

        customView.button_close.setOnClickListener{
            TransitionManager.beginDelayedTransition(rootCoordinatorLayout)
            mPopupWindow?.dismiss();
        }
        mPopupWindow?.setAnimationStyle(R.style.popup_window_animation)
        TransitionManager.beginDelayedTransition(rootCoordinatorLayout)
        mPopupWindow?.showAtLocation(customView.layout_popup, Gravity.TOP or Gravity.END,40,280)
    }


}// Required empty public constructor
