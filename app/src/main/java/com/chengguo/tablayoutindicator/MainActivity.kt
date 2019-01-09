package com.chengguo.tablayoutindicator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewPageAdapter by lazy {
        CommonViewPageAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewPager()
    }

    private fun initViewPager() {
        viewPageAdapter.fragmentPages.addAll(getFragmentPages())
        viewPager.adapter = viewPageAdapter
        tabLayout.setupWithViewPager(viewPager)
        tab_layout_title.mViewPager = viewPager
        tabLayoutIndicator0.mViewPager = viewPager
        tabLayoutIndicator1.mViewPager = viewPager
        tabLayoutIndicator2.mViewPager = viewPager
        tabLayoutIndicator3.mViewPager = viewPager
        tabLayoutIndicator4.mViewPager = viewPager
    }

    private fun getFragmentPages(): List<FragmentPage> =
        listOf(
            FragmentPage(PageFragment(), "关注"),
            FragmentPage(PageFragment(), "热门"),
            FragmentPage(PageFragment(), "附近")
        )
}
