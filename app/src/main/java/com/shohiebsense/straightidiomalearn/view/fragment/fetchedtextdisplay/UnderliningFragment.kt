package com.shohiebsense.straightidiomalearn.view.fragment.fetchedtextdisplay

import android.animation.Animator
import android.app.Fragment
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.straightidiomalearn.MainActivity
import com.shohiebsense.straightidiomalearn.R
import com.shohiebsense.straightidiomalearn.model.TranslatedIdiom
import com.shohiebsense.straightidiomalearn.model.UntranslatedIdiom
import com.shohiebsense.straightidiomalearn.services.emitter.DatabaseDataEmitter
import com.shohiebsense.straightidiomalearn.services.underline.UnderliningService
import com.shohiebsense.straightidiomalearn.utils.AppUtil
import com.shohiebsense.straightidiomalearn.utils.CircleAnimationUtil
import com.shohiebsense.straightidiomalearn.view.custom.CustomSnackbar
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.DatabaseCallback
import com.shohiebsense.straightidiomalearn.view.fragment.callbacks.FetchedTextCallback
import com.shohiebsense.straightidiomalearn.view.fragment.pdfdisplay.CardPagerAdapter
import com.shohiebsense.straightidiomalearn.view.items.IdiomMeaningItem
import com.shohiebsense.straightidiomalearn.view.items.IdiomMeaningViewHolder
import kotlinx.android.synthetic.main.fragment_underlining.*
import org.jetbrains.anko.act

/**
 * Created by Shohiebsense on 21/10/2017.
 *
 * susunan teks masih salah
 * cek string maksimum`
 *
 * masih lama
 *
 *
 * ON RESUME CEK ZRRAYLIST SIZE, KALAU 0 BALIK KE SPLASH
 *
 *
 * The factCardView must showing Indonesian translation not english
 */
class UnderliningFragment : Fragment(), FetchedTextCallback {

    lateinit var fetchedText : ArrayList<String>
    lateinit var underliningService: UnderliningService

    lateinit var fastAdapter : FastAdapter<IdiomMeaningItem>
    lateinit var itemAdapter : ItemAdapter<IdiomMeaningItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var snackbar : CustomSnackbar
    var idiomCounter = 0

    companion object {
        fun newInstance(name : ArrayList<String>?) : UnderliningFragment {
            val args = Bundle()
            args.putStringArrayList(MainActivity.fetchedTextMessage, name)
            val fragment = UnderliningFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(savedInstanceState == null){
            var arrayList = ArrayList<String>()
            arrayList.add(AppUtil.storyExample)
            fetchedText = arrayList
        }
        else{
            fetchedText = arguments.getStringArrayList(MainActivity.fetchedTextMessage)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_underlining, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackbar = CustomSnackbar.make(rootCoordinatorLayout,
                CustomSnackbar.LENGTH_INDEFINITE)
        snackbar.setText("please wait")
        snackbar.hidePermissionAction()
        var snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(act, R.color.secondaryLightColor))
        snackbar.show()
      //  DatabaseDataEmitter(activity, this).getAll()

        underliningService = UnderliningService(activity, this)

        itemAdapter = ItemAdapter.items()
        fastAdapter = FastAdapter.with(itemAdapter)
        idiomMeaningRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        idiomMeaningRecyclerView.adapter = fastAdapter
        onShowingBottomSheet()

        //commented for development
        underliningService.translate(fetchedText)


        //commented for development
        if(DatabaseDataEmitter.translatedIdiomList.size > 0 && DatabaseDataEmitter.untranslatedIdiomList.size > 0){
            underliningService.getUnderlineTheFetchedText(fetchedText)
            initAdapter()
        }
        else{
            DatabaseDataEmitter(activity,fetcCallback).getAll()
        }

    }

    fun initAdapter(){
        var adapter = CardPagerAdapter(activity)
        cardViewPager.adapter = adapter
        viewPagerIndicator.setupWithViewPager(cardViewPager)
        // fragmentFetchViewPagerIndicator.addOnPageChangeListener(mOnPageChangeListener)
        adapter.notifyDataSetChanged()

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






      /*  textFetchedTextView.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED

        }*/
    }

    var fetcCallback = object : DatabaseCallback {
        override fun onFetchingData(idiomMode: Int) {
        }

        override fun onErrorFetchingData() {
        }

        override fun onFetchedTranslatedData() {
        }

        override fun onFetchedUntranslatedData() {
            initAdapter()
        }
    }

    override fun onTranslatingText() {
       // toggleViews(UnderliningService.STATUS_LOADING)
    }
    override fun onFinishedTranslatingText() {
        cardViewPager.visibility = View.GONE
        viewPagerIndicator.visibility = View.GONE
        textFetchedTextView.visibility = View.VISIBLE
        // pdfDisplayerService.getUnderlineTheFetchedText()
    }

    override fun onErrorTranslatingText() {
      //  toggleErrorViews(UnderliningService.ERROR_TRANSLATE)
    }


    override fun onErrorUnderliningText(decoratedSpan: SpannableString) {
    }

    override fun onFinishedUnderliningText(decoratedSpan: SpannableString) {
        AppUtil.makeDebugLog("finished, not repeatable kan?")

        cardViewPager.visibility = View.GONE
        viewPagerIndicator.visibility = View.GONE
        textFetchedTextView.visibility = View.VISIBLE
        textFetchedTextView.movementMethod = LinkMovementMethod.getInstance()
        textFetchedTextView.setText(decoratedSpan)
        snackbar.dismiss()

    }


    override fun onErrorFetchingDatabase() {
       // toggleErrorViews(UnderliningService.ERROR_FETCH)
    }

    override fun onFetchingDatabase() {
       // toggleViews(UnderliningService.STATUS_LOADING)
    }

    override fun onFinishedFetchingTranslatedDatabase(translatedIdiomList: MutableList<TranslatedIdiom>) {
        //toggleViews(UnderliningService.STATUS_FETCHED_DB)
        //commented due DatabaseDataEmitter had mutableList
       // pdfDisplayerService.translatedIdiomList = translatedIdiomList


        AppUtil.makeErrorLog("beres translatedIdiom list" + translatedIdiomList.size)
        translatedIdiomList.forEach{
            translatedIdiom ->

        }
    }

    override fun onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList: MutableList<UntranslatedIdiom>) {
        //  toggleViews(UnderliningService.STATUS_FETCHED_DB)

        //pdfDisplayerService.untranslatedIdiomList = untranslatedIdiomList
        //pdfDisplayerService.getUnderlineTheFetchedText(fetchedText)

        AppUtil.makeErrorLog("beres untranslated list "+ untranslatedIdiomList.size)
        //pdfDisplayerService.getUnderlineTheFetchedText()


    }


    override fun onFindingTranslatedIdiom() {
    }

    override fun onFinishedFindingTranslatedIdiom(anuu: String, decoratedSpan: SpannableString) {

        //the old way, just uncomment
        //pdfDisplayerService.getUnderlineTheFetchedWithUntranslatedText(anuu, decoratedSpan, clickableSpan)
        onFinishedUnderliningText(decoratedSpan)
    }

    override fun onFindingUntranslatedIdiom() {
    }

    override fun onFinishedUntranslatedIdiom() {
    }

    override fun onClickedIdiomText(idiomText : String) {
        //error,
        //pdfDisplayerService.getSingleTranslate(idiomText)
        var items = mutableListOf<IdiomMeaningItem>()
        if(idiomText.contains(",")){
            val regex = ","
            var idiomList = idiomText.split(regex).toMutableList()

            for(meanings in idiomList){
                AppUtil.makeDebugLog("many choices : "+meanings)
                var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(meanings,idiomItemClickedListener)
                items.add(idiomMeaningItem)
            }
        }
        else{
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(idiomText,idiomItemClickedListener)
            items.add(idiomMeaningItem)
        }

        AppUtil.makeDebugLog("whatis "+idiomText)

        idiomMeaningRecyclerView.layoutManager = GridLayoutManager(activity,2)
        itemAdapter.clear()
        itemAdapter.add(items)
        idiomMeaningRecyclerView.adapter = fastAdapter


        if(behaviour.state != BottomSheetBehavior.STATE_EXPANDED){
            behaviour.state = BottomSheetBehavior.STATE_EXPANDED
        }
        else{
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN

        }

    }

    override fun onErrorClickedIdiomText() {
        AppUtil.makeErrorLog("Translating idiom finiished")
    }

    override fun onTranslatingIdiomOneByOne() {

    }

    override fun onFinishedTranslatingIdiomOneByOne(combineStringMeaning: MutableList<String>) {

        // Snackbar.make(factCardView, idiomText, Snackbar.LENGTH_INDEFINITE).show()
        var items = mutableListOf<IdiomMeaningItem>()

        combineStringMeaning.forEach {
            it->
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(it,idiomItemClickedListener)
            idiomMeaningItem.withOnItemClickListener { v, adapter, item, position ->
                Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                true
            }
            items.add(idiomMeaningItem)
        }
        AppUtil.makeDebugLog("sizenya "+items.size)
        itemAdapter.add(items)
    }

    override fun onErrorTranslatingIdiomOneByOne() {

    }


    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(view: View) {
            ++idiomCounter
            makeFlyAnimation(view)
        }

    }

    private fun makeFlyAnimation(targetView: View) {
        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(1000).setDestView(collectIdiomButton).setAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //addItem()
                collectIdiomButton.text =  idiomCounter.toString()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        }).startAnimation()


    }

    override fun onStart() {
        super.onStart()

    }
}