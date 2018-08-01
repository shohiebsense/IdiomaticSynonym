package com.shohiebsense.idiomaticsynonym.view.activity.setting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.shohiebsense.idiomaticsynonym.view.items.SettingItem
import kotlinx.android.synthetic.main.activity_settings.*
import android.support.v7.widget.DividerItemDecoration
import com.shohiebsense.idiomaticsynonym.R


class SettingsActivity : AppCompatActivity() {
    lateinit var fastAdapter : FastAdapter<SettingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_white_24)
        toolbar.setNavigationOnClickListener { onBackPressed() }



        val itemAdapter = ItemAdapter.items<SettingItem>()
        fastAdapter = FastAdapter.with(itemAdapter)
        val settingsLanguage = SettingItem().withSetting(getString(R.string.language_setting), LanguageSettingActivity::class.java.name)
        itemAdapter.add(settingsLanguage)
        val layoutManager = LinearLayoutManager(this)
        recycler_setting.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this,
                layoutManager.getOrientation())
        recycler_setting.addItemDecoration(dividerItemDecoration)
        recycler_setting.adapter = fastAdapter
    }



}
