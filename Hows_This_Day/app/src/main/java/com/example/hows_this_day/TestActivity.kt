package com.example.hows_this_day

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    private val adapter by lazy { MainAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // 뷰페이저 어댑터 연결
        testView.adapter = TestActivity@adapter

        testView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

                tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_a_black)
                tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_b_black)
                tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_c_black)
                tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_d_black)

                when(position) {

                    0   ->    tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_a)
                    1   ->    tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_b)
                    2   ->    tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_c)
                    3   ->    tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_d)
                }



            }

        })

        // 탭 레이아웃에 뷰페이저 연결
        tabLayout.setupWithViewPager(testView)

        // 탭 레이아웃 초기화
        tabLayout.getTabAt(0)?.setIcon(R.drawable.ic_a)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.ic_b_black)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.ic_c_black)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.ic_d_black)
    }
}