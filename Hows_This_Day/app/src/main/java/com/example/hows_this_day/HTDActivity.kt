package com.example.hows_this_day

import android.app.TabActivity
import android.os.Bundle
import android.view.LayoutInflater



class HTDActivity : TabActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tabHost = tabHost

        LayoutInflater.from(this).inflate(R.layout.fragment_htd, tabHost.tabContentView, true)

        tabHost.addTab(
            tabHost.newTabSpec("tab1")
                .setIndicator("tab1")
                .setContent(R.id.view1)
        )
        tabHost.addTab(
            tabHost.newTabSpec("tab2")
                .setIndicator("tab2")
                .setContent(R.id.view2)
        )
        tabHost.addTab(
            tabHost.newTabSpec("tab3")
                .setIndicator("tab3")
                .setContent(R.id.view3)
        )
        tabHost.addTab(
            tabHost.newTabSpec("tab4")
                .setIndicator("tab4")
                .setContent(R.id.view4)
        )
    }
}