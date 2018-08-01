package com.shohiebsense.idiomaticsynonym.view.activity.home.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.view.activity.setting.SettingsActivity
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.view.items.StatisticItem
import kotlinx.android.synthetic.main.fragment_statistics.*

/**
 * Created by Shohiebsense on 08/09/2017.
 * display statistics,
 */

class StatisticsFragment : Fragment() {
    lateinit var fastAdapter : FastAdapter<StatisticItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bookmarkEmitter = BookmarkDataEmitter(context!!)
        val itemAdapter = ItemAdapter.items<StatisticItem>()
        fastAdapter = FastAdapter.with(itemAdapter)

        val bookCountItem = StatisticItem().withStatistic(getString(R.string.text_statistic_books_translated),bookmarkEmitter.getHowManyBookTranslated().toString())
        val idiomCountItem = StatisticItem().withStatistic(getString(R.string.text_statistic_idioms_found), bookmarkEmitter.getHowManyIdiomsFound().toString())
        val indexedSentenceCountItem = StatisticItem().withStatistic(getString(R.string.text_statistic_indexed_sentences_found), bookmarkEmitter.getHowManyIndexedSentencesFound().toString())

        itemAdapter.add(bookCountItem)
        itemAdapter.add(idiomCountItem)
        itemAdapter.add(indexedSentenceCountItem)

        statisticsRecyclerView.layoutManager = LinearLayoutManager(activity)
        statisticsRecyclerView.adapter = fastAdapter
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
}
