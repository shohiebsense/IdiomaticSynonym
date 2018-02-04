package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.IndexedSentence
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.view.adapter.MyIndexedSentenceListRecyclerViewAdapter
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_indexed_sentence.*

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
class IndexedSentenceFragment : Fragment(), BookmarkDataEmitter.IndexedSentenceCallback {

    // TODO: Customize parameters
    lateinit var indexedSentences: ArrayList<IndexedSentence>
    var lastId = 0
    private var mListener: OnListFragmentInteractionListener = object : OnListFragmentInteractionListener {
        override fun onListFragmentInteraction(item: IndexedSentence) {
            AppUtil.makeDebugLog("onclicked")
        }

    }

    companion object {
        // TODO: Customize parameter initialization
        fun newInstance(lastId : Int): IndexedSentenceFragment {
            val fragment = IndexedSentenceFragment()
            val args = Bundle()
            args.putInt(TranslatedDisplayActivity.INTENT_LAST_ID, lastId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastId = arguments!!.getInt(TranslatedDisplayActivity.INTENT_LAST_ID)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_indexed_sentence, container, false)


        // Set the adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bookmarkDataEmitter = BookmarkDataEmitter(activity!!)
        bookmarkDataEmitter.getAllIndexedSentenceBasedOnLastId(lastId,this)
    }


    override fun onFetched(indexedSentences: ArrayList<IndexedSentence>) {
        Completable.create {
            sentenceIndexRecyclerView.layoutManager = LinearLayoutManager(context)
            sentenceIndexRecyclerView.adapter = MyIndexedSentenceListRecyclerViewAdapter(indexedSentences, mListener)
            if(indexedSentences.isEmpty()){
                sentenceIndexTitleTextView.text = "Empty"
            }
           it.onComplete()
        }.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe()
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
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: IndexedSentence)
    }


}
