package com.chengguo.tablayoutindicator

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.TraceCompat
import com.chengguo.indicator.OnChangeResourceListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewPageAdapter by lazy {
        CommonViewPageAdapter(supportFragmentManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Debug.startMethodTracing("MainActivityCreate")
        TraceCompat.beginSection("MainActivityOnCreate")
        setContentView(R.layout.activity_main)
//        Debug.stopMethodTracing()
        initViewPager()
        TraceCompat.endSection()
    }

    private fun initViewPager() {
        viewPageAdapter.fragmentPages.addAll(getFragmentPages())
        viewPager.adapter = viewPageAdapter
//        tabLayout.setupWithViewPager(viewPager)
        tab_layout_title.mViewPager = viewPager
        tab_layout_title2.mViewPager = viewPager
        tab_layout_title3.mViewPager = viewPager
        tab_layout_title4.mViewPager = viewPager
        tab_layout_title5.mViewPager = viewPager
        tab_layout_title6.mViewPager = viewPager
        tab_layout_title7.mViewPager = viewPager
        tab_layout_title8.mViewPager = viewPager
        tabLayoutIndicator0.mViewPager = viewPager
        tabLayoutIndicator1.mViewPager = viewPager

        tab_layout_title.changeResourceListener = object : OnChangeResourceListener {
            override fun clickResource(resource: Int, position: Int) {
//                Log.e("-------", "---------resource= $resource------position=$position-----")
            }
        }
//        tabLayoutIndicator2.mViewPager = viewPager
//        tabLayoutIndicator3.mViewPager = viewPager
//        tabLayoutIndicator4.mViewPager = viewPager
    }

    private fun getFragmentPages(): List<FragmentPage> =
        listOf(
            FragmentPage(PageFragment(), "关"),
            FragmentPage(PageFragment(), "热门"),
            FragmentPage(PageFragment(), "附近的人")
        )
}
