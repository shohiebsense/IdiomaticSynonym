package com.shohiebsense.idiomaticsynonym.view.fragment.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.view.items.BookmarkItem
import kotlinx.android.synthetic.main.fragment_bookmarks.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BookmarksFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BookmarksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookmarksFragment : Fragment() {

    private lateinit var bookmarks : ArrayList<BookmarkedEnglish>
    lateinit var bookmarkFastAdapter : FastAdapter<BookmarkItem>
    lateinit var bookmarkItemAdapter : ItemAdapter<BookmarkItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            bookmarks = arguments!!.getParcelableArrayList<BookmarkedEnglish>(ARG_PARAM_BOOKMARKS)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bookmarkItemAdapter= ItemAdapter.items()
        bookmarkFastAdapter = FastAdapter.with(bookmarkItemAdapter)
        var items = mutableListOf<BookmarkItem>()

        emptyTextView.visibility = if(bookmarks.isEmpty()) View.VISIBLE else View.GONE
        bookmarks.forEach {
            it->
            var idiomMeaningItem = BookmarkItem().withSentence(it)
            items.add(idiomMeaningItem)
        }
        bookmarkItemAdapter.add(items)
        bookmarkRecyclerView.layoutManager = LinearLayoutManager(activity)
        bookmarkRecyclerView.adapter = bookmarkFastAdapter
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM_BOOKMARKS = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookmarksFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: ArrayList<BookmarkedEnglish>): BookmarksFragment {
            val fragment = BookmarksFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_PARAM_BOOKMARKS, param1)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
