package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import kotlinx.android.synthetic.main.fragment_english_result.*


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
    var lastId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        englishTextsTextView.movementMethod = LinkMovementMethod()
        val bookmarkDataEmitter = BookmarkDataEmitter(activity!!)
        bookmarkDataEmitter.getEnglishBookmark(lastId,this)



    }

    override fun onFetched(bookmark: BookmarkedEnglish) {
        englishTextsTextView.text = bookmark.english
    }


}// Required empty public constructor
