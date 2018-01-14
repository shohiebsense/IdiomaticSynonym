package com.shohiebsense.idiomaticsynonym.view.fragment

import android.animation.Animator
import android.app.AlertDialog
import android.app.Fragment
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.transition.TransitionManager
import android.view.*
import android.widget.TextView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.CircleAnimationUtil
import com.shohiebsense.idiomaticsynonym.view.custom.CustomSnackbar
import com.shohiebsense.idiomaticsynonym.MainActivity
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.UnderliningService
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.DatabaseCallback
import com.shohiebsense.idiomaticsynonym.view.fragment.callbacks.UnderliningCallback
import com.shohiebsense.idiomaticsynonym.view.fragment.pdfdisplay.CardPagerAdapter
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceItem
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceViewHolder
import com.spyhunter99.supertooltips.ToolTip
import com.spyhunter99.supertooltips.ToolTipManager
import kotlinx.android.synthetic.main.fragment_underlining.*
import org.jetbrains.anko.act


/**
 * Created by Shohiebsense on 21/10/2017.
 *
 * susunan teks masih salah
 * cek string maksimum`
 *
 *
 *
 *
 *
 * The factCardView must showing Indonesian translation not english
 */
class UnderliningFragment : Fragment(), UnderliningCallback, BookmarkDataEmitter.IndexedSentenceCallback, IndexedSentenceViewHolder.IndexedSentenceClickListener {


    lateinit var extractedPdfTexts: ArrayList<String>
    lateinit var underliningService: UnderliningService
    lateinit var idiomMeaningFastAdapter: FastAdapter<IdiomMeaningItem>
    lateinit var idiomMeaningItemAdapter: ItemAdapter<IdiomMeaningItem>
    lateinit var indexedSentenceFastAdapter : FastAdapter<IndexedSentenceItem>
    lateinit var indexedSentenceItemAdapter : ItemAdapter<IndexedSentenceItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var snackbar : CustomSnackbar

    //var isShowingIdiom = true

    lateinit var transitionsContainer : ViewGroup


    lateinit var goToTranslatedDisplayMenuItem : MenuItem
    var idiomCounter = 0
    var selectedIndex = 0
    var tooltips: ToolTipManager? = null
    var fileName = ""

    companion object {
        fun newInstance(name: ArrayList<String>?, fileName: String) : UnderliningFragment {
            val args = Bundle()
            args.putStringArrayList(MainActivity.INTENT_FETCHED_TEXT, name)
            args.putString(MainActivity.INTENT_FILENAME, fileName)
            val fragment = UnderliningFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //This is for development
        if(savedInstanceState == null){
            var arrayList = ArrayList<String>()
            arrayList.add(AppUtil.storyExample)
            extractedPdfTexts = arrayList
        } else{
        extractedPdfTexts = arguments.getStringArrayList(MainActivity.INTENT_FETCHED_TEXT)
            fileName = arguments.getString(MainActivity.INTENT_FILENAME)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        transitionsContainer = container!!
        val view = inflater.inflate(R.layout.fragment_underlining, container, false)
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_goto_translateddisplay, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        goToTranslatedDisplayMenuItem = menu.findItem(R.id.goToTranslatedDisplayOption).setVisible(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.goToTranslatedDisplayOption -> {
                val translatedList = ArrayList<String>()
                translatedList.addAll(underliningService.translatedFetchedPdfText)


                val intent = Intent(activity, TranslatedDisplayActivity::class.java)
                intent.putExtra(TranslatedDisplayActivity.INTENT_FILENAME, fileName)
                intent.putExtra(TranslatedDisplayActivity.INTENT_FETCHED_TEXT, textFetchedTextView.text)
                intent.putExtra(TranslatedDisplayActivity.INTENT_TRANSLATED_TEXT, translatedList)
                intent.putExtra(TranslatedDisplayActivity.INTENT_INDICES, underliningService.indices)
                intent.putExtra(TranslatedDisplayActivity.INTENT_IDIOM_LIST, underliningService.indexedSentences)

                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Finding Idiom(s) ..."
        snackbar = CustomSnackbar.make(rootCoordinatorLayout,
                CustomSnackbar.LENGTH_INDEFINITE).setText("pleasewait ").hidePermissionAction()
        snackbar.show()
        tooltips = ToolTipManager(act)


        var snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(act, R.color.secondaryLightColor))
      //  TranslatedAndUntranslatedDataEmitter(context, this).getAll()

        underliningService = UnderliningService(activity, extractedPdfTexts, this,fileName)

        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        idiomOrSentenceRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        idiomOrSentenceRecyclerView.adapter = idiomMeaningFastAdapter
        onShowingBottomSheet()

        //commented for development
        //underliningService.translate()


        //commented for development
      /*  if(TranslatedAndUntranslatedDataEmitter.translatedIdiomList.size > 0 && TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.size > 0){
            //underliningService.getUnderLineZipping()
            initAdapter()
        }*/
        if(!TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty()){
            underliningService.underLine()
        }
        else{
            TranslatedAndUntranslatedDataEmitter(activity,fetcCallback).getAll()
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
        override fun onFetchedBoth() {

        }

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

/*        cardViewPager.visibility = View.GONE
        viewPagerIndicator.visibility = View.GONE
        textFetchedScrollView.visibility = View.VISIBLE*/
        // pdfDisplayerService.getUnderlineTheFetchedText()

        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    goToTranslatedDisplayMenuItem.setVisible(true)
                    activity.title = "Complete"
                    showMessageDialog()
                }
            }
        }.start()
    }

    override fun onErrorTranslatingText() {
      //  toggleErrorViews(UnderliningService.ERROR_TRANSLATE)
    }


    override fun onErrorUnderliningText() {
    }

    override fun onFinishedUnderliningText(decoratedSpan: ArrayList<CharSequence>) {

        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    AppUtil.makeDebugLog("finished, not repeatable kan? "+decoratedSpan.size)

                    cardViewPager.visibility = View.GONE
                    viewPagerIndicator.visibility = View.GONE
                    textFetchedTextView.visibility = View.VISIBLE
                    textFetchedTextView.movementMethod = LinkMovementMethod.getInstance()

                    decoratedSpan.forEach {
                        textFetchedTextView.append(it)
                    }
                    snackbar.dismiss()
                }
            }
        }.start()

    }


    override fun onErrorFetchingDatabase() {
       // toggleErrorViews(UnderliningService.ERROR_FETCH)
    }

    override fun onFetchingDatabase() {
       // toggleViews(UnderliningService.STATUS_LOADING)
    }

    override fun onFinishedFetchingTranslatedDatabase(translatedIdiomList: MutableList<TranslatedIdiom>) {
        //toggleViews(UnderliningService.STATUS_FETCHED_DB)
        //commented due TranslatedAndUntranslatedDataEmitter had mutableList
       // pdfDisplayerService.translatedIdiomList = translatedIdiomList


        AppUtil.makeErrorLog("beres translatedIdiom list" + translatedIdiomList.size)
        translatedIdiomList.forEach{
            translatedIdiom ->

        }
    }

    override fun onFinishedFetchingUntranslatedDatabase(untranslatedIdiomList: MutableList<UntranslatedIdiom>) {
        //  toggleViews(UnderliningService.STATUS_FETCHED_DB)

        //pdfDisplayerService.untranslatedIdiomList = untranslatedIdiomList
        //pdfDisplayerService.getUnderlineTheFetchedText(extractedPdfTexts)

        AppUtil.makeErrorLog("beres untranslated list "+ untranslatedIdiomList.size)
        //pdfDisplayerService.getUnderlineTheFetchedText()


    }


    override fun onFindingTranslatedIdiom() {
    }

    override fun onFinishedFindingTranslatedIdiom(decoratedSpan: java.util.ArrayList<CharSequence>) {

        //the old way, just uncomment
        //pdfDisplayerService.getUnderlineTheFetchedWithUntranslatedText(anuu, decoratedSpan, clickableSpan)

        onFinishedUnderliningText(decoratedSpan)
    }

    override fun onFindingUntranslatedIdiom() {
    }

    override fun onFinishedUntranslatedIdiom() {
    }

    override fun onClickedIdiomText(idiomText: String, index: Int, sentence: String) {
        //error,
        //pdfDisplayerService
        // .getSingleTranslate(idiomText)
        //getTheselectedIndex

        //if(isShowingIdiom){
            if(idiomText.isBlank()){
                return;
            }
            selectedIndex = index
            var items = mutableListOf<IdiomMeaningItem>()
            if(idiomText.contains(",")){
                val regex = ","
                var idiomList = idiomText.split(regex).toMutableList()

                for(meanings in idiomList){
                    AppUtil.makeDebugLog("many choices : "+meanings)
                    var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(meanings,idiomItemClickedListener,index)
                    items.add(idiomMeaningItem)
                }
                // selectedIdiomList!!.put(index,idiomList.first())
            }
            else{

                var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(idiomText,idiomItemClickedListener,index)
                items.add(idiomMeaningItem)
            }

            idiomOrSentenceRecyclerView.layoutManager = GridLayoutManager(activity,2)
            idiomMeaningItemAdapter.clear()
            idiomMeaningItemAdapter.add(items)
            idiomOrSentenceRecyclerView.adapter = idiomMeaningFastAdapter
        //}
      /*  else{
            getIndexedSentence()
        }*/

        toggleBottomSheet()

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


    override fun onErrorClickedIdiomText() {
        AppUtil.makeErrorLog("Translating idiom finiished")
    }

    override fun onTranslatingIdiomOneByOne() {

    }

    override fun onFinishedTranslatingIdiomOneByOne(combineStringMeaning: MutableList<String>, sentenceIndex: Int) {
        // Snackbar.make(factCardView, idiomText, Snackbar.LENGTH_INDEFINITE).show()
        var items = mutableListOf<IdiomMeaningItem>()


        combineStringMeaning.forEach {
            it->
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(it,idiomItemClickedListener,sentenceIndex)
            items.add(idiomMeaningItem)
        }
        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    idiomMeaningItemAdapter.clear()
                    idiomMeaningItemAdapter.add(items)
                    idiomOrSentenceRecyclerView.adapter = idiomMeaningFastAdapter

                   toggleBottomSheet()
                }
            }
        }.start()

    }

    override fun onErrorTranslatingIdiomOneByOne() {

    }


    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(view: TextView, index: Int) {
            ++idiomCounter
            TransitionManager.beginDelayedTransition(transitionsContainer)
            AppUtil.makeDebugLog("selectedIndex = "+index)
            makeFlyAnimation(view)
            addToToolTipView(underliningService.indexedSentences[index].sentence)

        }

    }

    fun addToToolTipView(text : String){
        var toolTip = ToolTip()
                .withText(text)
                .withColor(ContextCompat.getColor(act,R.color.slightlyYellowColor))
                .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                .withShadow();
        tooltips!!.showToolTip(toolTip, toolTipsView);
    }

    private fun makeFlyAnimation(targetView: TextView) {
        CircleAnimationUtil().attachActivity(activity).setTargetView(targetView).setMoveDuration(1000).setDestView(toolTipsView).setAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                //addItem()
                collectIdiomButton.text =  idiomCounter.toString()
                targetView.setVisibility(View.VISIBLE)

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        }).startAnimation()


    }

    fun showMessageDialog(){
       /* var dialog = FindIdiomsDialogFragment()
        dialog.setTargetFragment(this, 1)
        dialog.show(fragmentManager, FindIdiomsDialogFragment::class.java.simpleName)
*/
        val builder: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = AlertDialog.Builder(act, R.style.MyDialogTheme)
        } else {
            builder = AlertDialog.Builder(act)
        }
        builder.setTitle(getString(R.string.dialog_title_find_idioms))
                .setMessage(getString(R.string.dialog_message_find_idioms))
                .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                    // continue with delete
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }

    fun getIndexedSentence(){
        BookmarkDataEmitter(activity).getAllIndexedSentenceBasedOnPdfFileName("",this)
    }

    override fun onFetched(indexedSentences: List<IndexedSentence>) {
        AppUtil.makeDebugLog("indexed sentences size "+indexedSentences.size)
        var items = mutableListOf<IndexedSentenceItem>()

        indexedSentences.forEach {
            var indexedSentenceItem = IndexedSentenceItem().withSentence(it,this)
            items.add(indexedSentenceItem)
        }

        idiomOrSentenceRecyclerView.layoutManager = LinearLayoutManager(act)
        indexedSentenceItemAdapter.clear()
        indexedSentenceItemAdapter.add(items)
        idiomOrSentenceRecyclerView.adapter = idiomMeaningFastAdapter

    }

    override fun onItemClick(index: Int) {

    }


}