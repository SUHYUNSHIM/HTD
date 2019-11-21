package com.example.hows_this_day

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tap.*

class TabActivity : AppCompatActivity() {

    private val adapter by lazy { MainAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap)

        // 뷰페이저 어댑터 연결
        testView.adapter = TabActivity@adapter

        testView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {

                tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_black)
                tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_black)
                tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_black)
                tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_black)

                when(position) {

                    0   ->    tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_white)
                    1   ->    tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_white)
                    2   ->    tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_white)
                    3   ->    tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_white)
                }



            }

        })

        // 탭 레이아웃에 뷰페이저 연결
        tabLayout.setupWithViewPager(testView)

        // 탭 레이아웃 초기화
        tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_white)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_black)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_black)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_black)
    }
}