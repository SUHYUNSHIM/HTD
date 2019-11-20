package com.example.hows_this_day

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.hows_this_day.fragment.*

class MainAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragmentTitleList = mutableListOf("A","B","C","D")

    override fun getItem(position: Int): Fragment? {

        return when(position) {

            0       ->  AFragment()

            1       ->  BFragment()

            2       ->  CFragment()

            3       ->  DFragment()

            else    ->  null
        }

    }

    // 생성 할 Fragment 의 개수
    override fun getCount() = 4

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
        //Log.e("FragmentPagerAdapter", "destroyItem position : $position")
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }

}