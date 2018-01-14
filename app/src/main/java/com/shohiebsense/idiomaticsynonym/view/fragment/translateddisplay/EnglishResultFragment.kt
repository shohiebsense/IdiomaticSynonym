package com.shohiebsense.idiomaticsynonym.view.fragment.translateddisplay


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.TranslatedDisplayActivity
import kotlinx.android.synthetic.main.fragment_english_result.*


/**
 * A simple [Fragment] subclass.
 * Use the [EnglishResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EnglishResultFragment : Fragment() {

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
        fun newInstance(fileName: String, texts: String): EnglishResultFragment {
            val fragment = EnglishResultFragment()
            val args = Bundle()
            args.putString(TranslatedDisplayActivity.INTENT_FILENAME, fileName)
            args.putString(TranslatedDisplayActivity.INTENT_FETCHED_TEXT, texts)
            fragment.arguments = args
            return fragment
        }
    }



    // TODO: Rename and change types of parameters
    private var fileName: String? = null
    private var englishTexts: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            fileName = arguments!!.getString(TranslatedDisplayActivity.INTENT_FILENAME)
            englishTexts = arguments!!.getString(TranslatedDisplayActivity.INTENT_FETCHED_TEXT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_english_result, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        englishTextsTextView.text = englishTexts

    }
}// Required empty public constructor
