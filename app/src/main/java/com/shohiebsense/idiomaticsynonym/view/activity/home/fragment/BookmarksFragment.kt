package com.shohiebsense.idiomaticsynonym.view.activity.home.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.view.activity.home.MainActivity

import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.activity.setting.SettingsActivity
import com.shohiebsense.idiomaticsynonym.model.BookmarkedEnglish
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
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
class BookmarksFragment : Fragment(), BookmarkDataEmitter.BookmarksCallback {

    lateinit var bookmarkFastAdapter : FastAdapter<BookmarkItem>
    lateinit var bookmarkItemAdapter : ItemAdapter<BookmarkItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmarks, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity as MainActivity).bookmarkEmitter.getEnglisbBookmarks(this)

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_main, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.settingMenuOption -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
            }
        }
        return true
    }

    override fun onFetched(bookmarks: ArrayList<BookmarkedEnglish>) {
        bookmarkItemAdapter= ItemAdapter.items()
        bookmarkFastAdapter = FastAdapter.with(bookmarkItemAdapter)
        var items = mutableListOf<BookmarkItem>()
        emptyTextView.visibility = if(bookmarks.isEmpty()) View.VISIBLE else View.GONE
        bookmarks.forEach {
            it->
            var idiomMeaningItem = BookmarkItem().withSentence(it).withSelectable(true)
            items.add(idiomMeaningItem)
        }
        bookmarkItemAdapter.add(items)
        bookmarkRecyclerView.layoutManager = LinearLayoutManager(activity)
        bookmarkRecyclerView.adapter = bookmarkFastAdapter
    }

    override fun onError() {
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
        fun newInstance(): BookmarksFragment {
            val fragment = BookmarksFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
