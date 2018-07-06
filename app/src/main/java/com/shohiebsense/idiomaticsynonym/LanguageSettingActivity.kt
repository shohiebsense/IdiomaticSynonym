package com.shohiebsense.idiomaticsynonym

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.model.ChosenLanguage
import com.shohiebsense.idiomaticsynonym.utils.AppUtil
import com.shohiebsense.idiomaticsynonym.utils.LocaleManager
import com.shohiebsense.idiomaticsynonym.view.items.LanguageSettingItem
import kotlinx.android.synthetic.main.activity_language_setting.*

class LanguageSettingActivity : AppCompatActivity(), LanguageSettingItem.SettingItemClickListener {

    lateinit var fastAdapter : FastAdapter<LanguageSettingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_setting)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val itemAdapter = ItemAdapter.items<LanguageSettingItem>()
        fastAdapter = FastAdapter.with(itemAdapter)

        val englishLanguageItem = LanguageSettingItem(this, ChosenLanguage(getString(R.string.english),LocaleManager.LANGUAGE_ENGLISH))
        val indonesianLanguageItem = LanguageSettingItem(this,ChosenLanguage(getString(R.string.indonesian),LocaleManager.LANGUAGE_INDONESIAN))

        itemAdapter.add(englishLanguageItem)
        itemAdapter.add(indonesianLanguageItem)
        val layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this,
                layoutManager.getOrientation())
        recycler_language_setting.layoutManager = layoutManager
        recycler_language_setting.addItemDecoration(dividerItemDecoration)
        recycler_language_setting.adapter = fastAdapter

    }

    override fun onItemClick(language: String) {
        if(language.equals(getString(R.string.indonesian))){
            LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_INDONESIAN)
        }
        else if(language.equals(getString(R.string.english))){
            AppUtil.makeErrorLog("english selecteddd")
            LocaleManager.setNewLocale(this, LocaleManager.LANGUAGE_ENGLISH)
        }
        finishAffinity()
        AppUtil.restartApplication(this)
    }




}
