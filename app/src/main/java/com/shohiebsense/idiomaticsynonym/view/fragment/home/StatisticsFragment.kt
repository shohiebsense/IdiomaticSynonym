package com.shohiebsense.idiomaticsynonym.view.fragment.home

import android.media.audiofx.BassBoost
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.R
import com.shohiebsense.idiomaticsynonym.services.emitter.BookmarkDataEmitter
import com.shohiebsense.idiomaticsynonym.view.items.StatisticItem
import kotlinx.android.synthetic.main.fragment_statistics.*

/**
 * Created by Shohiebsense on 08/09/2017.
 * display statistics,
 */

class StatisticsFragment : Fragment() {
    lateinit var fastAdapter : FastAdapter<StatisticItem>

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
}
