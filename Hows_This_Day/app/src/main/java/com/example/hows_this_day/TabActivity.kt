package com.example.hows_this_day

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_tab.*


class TabActivity : AppCompatActivity() {

    // adapter 늦은 초기화
    private val adapter by lazy { MainAdapter(supportFragmentManager) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)

        // 어댑터 연결
        testView.adapter = TabActivity@adapter

        testView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            // 손으로 밀어서 옆으로 넘기는 기능 override
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // tab이 선택되지 않았을 때 보여줄 아이콘
                tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_black)
                tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_black)
                tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_black)
                tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_black)

                when(position) {
                    // tab이 선택되었을 때 보여줄 이모티콘
                    0   ->    tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_white)
                    1   ->    tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_white)
                    2   ->    tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_white)
                    3   ->    tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_white)
                }



            }

        })

        // 탭 레이아웃에 뷰페이저 연결
        tabLayout.setupWithViewPager(testView)

        // 탭 레이아웃 초기화(기본적으로 0이 선택되므로 선택됐을 때 이모티콘으로 설정)
        tabLayout.getTabAt(0)?.setIcon(R.drawable.profile_white)
        tabLayout.getTabAt(1)?.setIcon(R.drawable.calendar_black)
        tabLayout.getTabAt(2)?.setIcon(R.drawable.diary_black)
        tabLayout.getTabAt(3)?.setIcon(R.drawable.setting_black)
    }
}