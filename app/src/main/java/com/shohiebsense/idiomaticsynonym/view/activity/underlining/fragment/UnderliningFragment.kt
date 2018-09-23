package com.shohiebsense.idiomaticsynonym.view.activity.underlining.fragment

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
import android.text.SpannableStringBuilder
import android.view.*
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.klinker.android.link_builder.TouchableMovementMethod
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.custom.CustomSnackbar
import com.shohiebsense.idiomaticsynonym.view.activity.detail.DetailActivity
import com.shohiebsense.idiomaticsynonym.view.activity.underlining.UnderliningActivity
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.model.TranslatedIdiom
import com.shohiebsense.idiomaticsynonym.model.UntranslatedIdiom
import com.shohiebsense.idiomaticsynonym.services.UnderliningServiceUsingContains
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.services.emitter.TranslatedAndUntranslatedDataEmitter
import com.shohiebsense.idiomaticsynonym.services.kateglo.KategloService
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.callbacks.DatabaseCallback
import com.shohiebsense.idiomaticsynonym.view.callbacks.UnderliningCallback
import com.shohiebsense.idiomaticsynonym.view.custom.EmptyResultDialogFragment
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceItem
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceViewHolder
import com.spyhunter99.supertooltips.ToolTip
import com.spyhunter99.supertooltips.ToolTipManager
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_underlining.*
import kotlinx.android.synthetic.main.fragment_underlining2.*
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
class UnderliningFragment : Fragment(), UnderliningCallback, BookmarkDataEmitter.IndexedSentenceCallback, IndexedSentenceViewHolder.IndexedSentenceClickListener, KategloService.KategloListener {



    val KEY_PROCESSEDTEXT = "processed_text"
    var KEY_STATE = 0
    lateinit var extractedPdfTexts: CharSequence
    lateinit var underliningService: UnderliningServiceUsingContains
    lateinit var idiomMeaningFastAdapter: FastAdapter<IdiomMeaningItem>
    lateinit var idiomMeaningItemAdapter: ItemAdapter<IdiomMeaningItem>
    lateinit var indexedSentenceItemAdapter : ItemAdapter<IndexedSentenceItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    var translated = false
    var underlined = false
    var currentClickedWord = ""
    lateinit var snackbar : CustomSnackbar

    lateinit var kategloService : KategloService

    var lastId = -1
    //var isShowingIdiom = true

    lateinit var transitionsContainer : ViewGroup
    lateinit var toolbar : android.support.v7.widget.Toolbar


    var goToTranslatedDisplayMenuItem : MenuItem? = null
    var idiomCounter = 0
    var selectedIndex = 0
    var tooltips: ToolTipManager? = null
    var fileName = ""
    val sentences = SpannableStringBuilder("")

    companion object {
        fun newInstance(name: ArrayList<String>?, fileName: String, lastId: Int) : UnderliningFragment {
            val args = Bundle()
            args.putString(UnderliningActivity.INTENT_FILENAME, fileName)
            args.putInt(UnderliningActivity.INTENT_ID, lastId)
            val fragment = UnderliningFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        //This is for development

        lastId = arguments.getInt(UnderliningActivity.INTENT_ID)
        extractedPdfTexts = BookmarkDataEmitter(act).getEnglishTextBasedOnId(lastId)
        fileName = arguments.getString(UnderliningActivity.INTENT_FILENAME)
        underliningService = UnderliningServiceUsingContains(activity, extractedPdfTexts, this,fileName)
        kategloService = KategloService()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        transitionsContainer = container!!
        val view = inflater.inflate(R.layout.fragment_underlining2, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = (activity as UnderliningActivity).toolbar
        toolbar.title = getString(R.string.process_finding_idiom)
        activity.invalidateOptionsMenu()

        behaviour = BottomSheetBehavior.from(bottomSheetLayout)
        tooltips = ToolTipManager(act)
        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        initBottomSheet()
        bottomSheetLayout.setOnClickListener {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
        }
        if(KEY_STATE == 0){
            if(!TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty()){
                underliningService.underLine(behaviour)
            }
            else{
                TranslatedAndUntranslatedDataEmitter(activity,fetcCallback).getAll()
            }
        }

        snackbar = CustomSnackbar.make(rootCoordinatorLayout,
                CustomSnackbar.LENGTH_INDEFINITE).setText("please wait ").hidePermissionAction()
        var snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(act, R.color.secondaryDarkColor))
        snackbar.show()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_goto_translateddisplay, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        goToTranslatedDisplayMenuItem = menu.findItem(R.id.goToTranslatedDisplayOption).setVisible(false)
        AppUtil.makeErrorLog("is it null again  "+(goToTranslatedDisplayMenuItem != null))
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.goToTranslatedDisplayOption -> {
                val intent = Intent(activity, DetailActivity::class.java)
                AppUtil.makeDebugLog("filenamee is ? "+fileName)
                intent.putExtra(DetailActivity.INTENT_LAST_ID, lastId)
                intent.putExtra(DetailActivity.INTENT_FILENAME, fileName)
                intent.putExtra(DetailActivity.INTENT_IS_TRANSLATION_EMPTY,false)
                intent.putExtra(DetailActivity.INTENT_IS_FROM_BOOKMARK_ITEM,false)
                startActivity(intent)
                activity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStart() {
        super.onStart()
        if(KEY_STATE == 1 || KEY_STATE == 2 && !fileName.isBlank()){
            activity.title = fileName
            if(goToTranslatedDisplayMenuItem != null){
                goToTranslatedDisplayMenuItem!!.isVisible = true
            }
        }
    }

    fun initAdapter(){


    }

    fun initBottomSheet(){
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

    override fun onTranslatingText(index: Int) {
        Observable.just{
            toolbar.title = getString(R.string.done)
            String.format(getString(R.string.translating),index,underliningService.numberofPages)
        }.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe()
    }
    override fun onFinishedTranslatingText() {

        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    if(activity != null){
                        showSuccessfulMessageDialog()
                        toolbar.title = getString(R.string.done)
                        addToToolTipView(getString(R.string.explore_idioms))
                        translated = true
                        AppUtil.makeErrorLog("not hello "+translated+ "  "+underlined+ "   "+goToTranslatedDisplayMenuItem)
                        if(translated && underlined && goToTranslatedDisplayMenuItem != null){
                            goToTranslatedDisplayMenuItem?.isVisible = true

                        }
                    }

                }
            }
        }.start()

    }


    override fun onErrorTranslatingText(error : String) {
        AppUtil.showSnackbar(activity,AppUtil.SNACKY_ERROR,error)
        toolbar.title = fileName
        addToToolTipView(getString(R.string.error_check_your_connection))
        translated = false
        if(translated && underlined && goToTranslatedDisplayMenuItem != null){
            goToTranslatedDisplayMenuItem?.isVisible = true

        }
    }


    override fun onErrorUnderliningText() {
    }

    override fun onFinishedUnderliningText(decoratedSpan: ArrayList<Link>) {
        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    AppUtil.makeDebugLog("underlining process finished "+decoratedSpan.size)
                    cardViewPager.visibility = View.GONE
                    textFetchedTextView.visibility = View.VISIBLE
                    activity.title = fileName
                    var spannableString  : CharSequence = ""
                    val charSequence = LinkBuilder.from(act,extractedPdfTexts.toString()).addLinks(decoratedSpan).build()
                    textFetchedTextView.setText(charSequence)
                    textFetchedTextView.movementMethod = TouchableMovementMethod.instance
                    snackbar.dismiss()
                    addToToolTipView(getString(R.string.dialog_find_idioms_and_replaced_it))
                    toolbar.title = getString(R.string.translating)
                    underlined = true
                    KEY_STATE = 1
                    AppUtil.makeErrorLog("finished helloww "+underlined +"  and "+translated)
                    if(underlined && translated){
                        KEY_STATE = 2
                        goToTranslatedDisplayMenuItem?.isVisible = true
                        act.invalidateOptionsMenu()
                    }
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

    override fun onFinishedFindingTranslatedIdiom(decoratedSpan: java.util.ArrayList<SpannableStringBuilder>) {

        //the old way, just uncomment
        //pdfDisplayerService.getUnderlineTheFetchedWithUntranslatedText(anuu, decoratedSpan, clickableSpan)

        //onFinishedUnderliningText(decoratedSpan)
    }

    override fun onFindingUntranslatedIdiom() {
    }

    override fun onFinishedUntranslatedIdiom() {
    }

    override fun onClickedIdiomText(idiomText: String) {
        var items = mutableListOf<IdiomMeaningItem>()
        if(idiomText.contains(",")){
            var idiomList = idiomText.split(",").toMutableList()
            for(meanings in idiomList){
                var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(meanings,idiomItemClickedListener)
                items.add(idiomMeaningItem)
            }
        }
        else{
            var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(idiomText,idiomItemClickedListener)
            items.add(idiomMeaningItem)
        }
        Completable.create {
            idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
            idiomMeaningItemAdapter.clear()
            idiomMeaningItemAdapter.add(items)
            idiomRecyclerView.adapter = idiomMeaningFastAdapter
            toggleBottomSheet()
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }


    fun toggleBottomSheet(){
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

    override fun onErrorTranslatingIdiomOneByOne() {

    }


    var idiomItemClickedListener = object : IdiomMeaningViewHolder.IdiomItemClickListener {
        override fun onIdiomItemClick(word: String) {
            behaviour.state = BottomSheetBehavior.STATE_HIDDEN
            currentClickedWord = word
            kategloService.getSynonymStrings(word,this@UnderliningFragment)
        }
    }

    override fun onGetSynonyms(synonyms: MutableList<String>) {
        synonyms.add(0,currentClickedWord)
        if(synonyms.isEmpty()){
            return;
        }
        var items = mutableListOf<IdiomMeaningItem>()
        for(synonym in synonyms){
            var synonymItem = IdiomMeaningItem().withIdiomMeaning(synonym,idiomItemClickedListener)
            items.add(synonymItem)
        }
        Completable.create {
            idiomRecyclerView.layoutManager = GridLayoutManager(activity,2)
            idiomMeaningItemAdapter.clear()
            idiomMeaningItemAdapter.add(items)
            idiomRecyclerView.adapter = idiomMeaningFastAdapter
            toggleBottomSheet()
        }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
    }


    fun addToToolTipView(text : String){
        var toolTip = ToolTip()
                .withText(text)
                .withColor(ContextCompat.getColor(act,R.color.slightlyYellowColor))
                .withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW)
                .withShadow();
        tooltips!!.showToolTip(toolTip, toolTipsView);
    }


    fun showSuccessfulMessageDialog(){
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

    override fun onFetched(indexedSentences: ArrayList<IndexedSentence>) {
        AppUtil.makeDebugLog("indexed sentences size "+indexedSentences.size)
        var items = mutableListOf<IndexedSentenceItem>()

        indexedSentences.forEach {
            var indexedSentenceItem = IndexedSentenceItem().withSentence(it,this)
            items.add(indexedSentenceItem)
        }

        idiomRecyclerView.layoutManager = LinearLayoutManager(act)
        indexedSentenceItemAdapter.clear()
        indexedSentenceItemAdapter.add(items)
        idiomRecyclerView.adapter = idiomMeaningFastAdapter

    }

    override fun onItemClick(index: Int) {

    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if(outState != null){
            outState.putCharSequence(KEY_PROCESSEDTEXT, sentences)
        }
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null){
            textFetchedTextView.setText(savedInstanceState.getString(KEY_PROCESSEDTEXT))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    fun showEmptyResultDialog(){
        var dialog = EmptyResultDialogFragment()
        dialog.setTargetFragment(this, 1)
        dialog.show(fragmentManager, EmptyResultDialogFragment::class.java.simpleName)

    }

    override fun onErrorTranslating() {
        if(activity != null){
            Completable.create {
                AppUtil.showSnackbar(activity,AppUtil.SNACKY_ERROR,getString(R.string.error_check_your_connection))
                tooltips!!.closeActiveTooltip()
                tooltips!!.closeTooltipImmediately()
                toolbar.title = "Check Your Connection"
            }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread()).subscribe()
        }
    }


}