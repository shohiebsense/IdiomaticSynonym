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
import android.text.SpannableStringBuilder
import android.transition.TransitionManager
import android.view.*
import android.widget.TextView
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.LinkBuilder
import com.klinker.android.link_builder.TouchableMovementMethod
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.utils.CircleAnimationUtil
import com.shohiebsense.idiomaticsynonym.view.custom.CustomSnackbar
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.UnderliningActivity
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
import com.shohiebsense.idiomaticsynonym.view.adapter.CardPagerAdapter
import com.shohiebsense.idiomaticsynonym.view.custom.EmptyResultDialogFragment
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningItem
import com.shohiebsense.idiomaticsynonym.view.items.IdiomMeaningViewHolder
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceItem
import com.shohiebsense.idiomaticsynonym.view.items.IndexedSentenceViewHolder
import com.spyhunter99.supertooltips.ToolTip
import com.spyhunter99.supertooltips.ToolTipManager
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
    lateinit var indexedSentenceFastAdapter : FastAdapter<IndexedSentenceItem>
    lateinit var indexedSentenceItemAdapter : ItemAdapter<IndexedSentenceItem>
    lateinit var behaviour : BottomSheetBehavior<View>
    lateinit var snackbar : CustomSnackbar
    var translated = false
    var underlined = false
    var currentClickedWord = ""

    lateinit var kategloService : KategloService

    var lastId = -1
    //var isShowingIdiom = true

    lateinit var transitionsContainer : ViewGroup


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
                val intent = Intent(activity, TranslatedDisplayActivity::class.java)
                AppUtil.makeDebugLog("filenamee is ? "+fileName)
                intent.putExtra(TranslatedDisplayActivity.INTENT_LAST_ID, lastId)
                intent.putExtra(TranslatedDisplayActivity.INTENT_FILENAME, fileName)
                intent.putExtra(TranslatedDisplayActivity.INTENT_IS_FROM_BOOKMARKITEM,false)
                startActivity(intent)
                activity.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.title = "Finding Idiom(s) ..."
        activity.invalidateOptionsMenu()
        snackbar = CustomSnackbar.make(rootCoordinatorLayout,
                CustomSnackbar.LENGTH_INDEFINITE).setText("pleasewait ").hidePermissionAction()
        snackbar.show()


        tooltips = ToolTipManager(act)

        //textFetchedTextView.setHtml(extractedPdfTexts.toString())
        var snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(act, R.color.secondaryLightColor))
      //  TranslatedAndUntranslatedDataEmitter(context, this).getAll()


        idiomMeaningItemAdapter = ItemAdapter.items()
        idiomMeaningFastAdapter = FastAdapter.with(idiomMeaningItemAdapter)
        idiomRecyclerView.layoutManager = GridLayoutManager(activity,2) as GridLayoutManager
        idiomRecyclerView.adapter = idiomMeaningFastAdapter
        onShowingBottomSheet()

        //commented for development
        //underliningService.translate()


        //commented for development
      /*  if(TranslatedAndUntranslatedDataEmitter.translatedIdiomList.size > 0 && TranslatedAndUntranslatedDataEmitter.untranslatedIdiomList.size > 0){
            //underliningService.getUnderLineZipping()
            initAdapter()
        }*/
        if(KEY_STATE == 0){
            if(!TranslatedAndUntranslatedDataEmitter.idiomsList.isEmpty()){
                underliningService.underLine(behaviour)
            }
            else{
                TranslatedAndUntranslatedDataEmitter(activity,fetcCallback).getAll()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        if(KEY_STATE == 1 || KEY_STATE == 2 && !fileName.isBlank()){
            cardViewPager.visibility = View.GONE
            textFetchedTextView.visibility = View.VISIBLE
            activity.title = fileName
            if(goToTranslatedDisplayMenuItem != null){
                goToTranslatedDisplayMenuItem!!.isVisible = true
            }
        }
    }

    fun initAdapter(){
        var adapter = CardPagerAdapter(activity)
        cardViewPager.adapter = adapter
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

        object : Thread(){
            override fun start() {
                Handler(Looper.getMainLooper()).post {
                    activity.title = fileName
                    showMessageDialog()
                    translated = true
                    AppUtil.makeErrorLog("not hello "+translated+ "  "+underlined+ "   "+goToTranslatedDisplayMenuItem)
                    if(translated && underlined && goToTranslatedDisplayMenuItem != null){
                        goToTranslatedDisplayMenuItem?.isVisible = true

                    }
                }
            }
        }.start()

    }


    override fun onErrorTranslatingText() {
      //  toggleErrorViews(UnderliningService.ERROR_TRANSLATE)
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
                  /*  decoratedSpan.forEach {
                      spannableString = TextUtils.concat(spannableString,it.sentence)
                    }*/

                    val charSequence = LinkBuilder.from(act,extractedPdfTexts.toString()).addLinks(decoratedSpan).build()
                    textFetchedTextView.setText (charSequence)

                    textFetchedTextView.movementMethod = TouchableMovementMethod.instance

                    //commented
                   // underliningService.bookmarkDataEmitter.updateEnglishText(AppUtil.toHtml(act, charSequence!!))
                    snackbar.dismiss()
               /*     if(decoratedSpan.isEmpty()){
                        showEmptyResultDialog()
                    }*/
                    addToToolTipView(getString(R.string.dialog_find_idioms_and_replaced_it))
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
        //error,
        //pdfDisplayerService
        // .getSingleTranslate(idiomText)
        //getTheselectedIndex

        //if(isShowingIdiom){
            if(idiomText.isBlank()){
                return;
            }
            var items = mutableListOf<IdiomMeaningItem>()
            if(idiomText.contains(",")){
                val regex = ","
                var idiomList = idiomText.split(regex).toMutableList()

                for(meanings in idiomList){
                    AppUtil.makeDebugLog("many choices : "+meanings)
                    var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(meanings,idiomItemClickedListener)
                    items.add(idiomMeaningItem)
                }
                // selectedIdiomList!!.put(index,idiomList.first())
            }
            else{

                var idiomMeaningItem = IdiomMeaningItem().withIdiomMeaning(idiomText,idiomItemClickedListener)
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

    override fun onGetSyonyms(synonyms: MutableList<String>) {
        synonyms.add(0,currentClickedWord)
        if(synonyms.isEmpty()){
            return;
        }
        var items = mutableListOf<IdiomMeaningItem>()

        for(synonym in synonyms){
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
               /// replaceSentenceButton.text =  idiomCounter.toString()
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

}