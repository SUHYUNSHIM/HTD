package com.example.hows_this_day.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.hows_this_day.BaseCalendar
import com.example.hows_this_day.R
import com.example.hows_this_day.RecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_b.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.text.SimpleDateFormat
import java.util.*

class BFragment : Fragment() {

    lateinit var scheduleRecyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //뷰 설정
        tvFragmentMain
        initView()
    }

    fun initView() {

        scheduleRecyclerViewAdapter = RecyclerViewAdapter(this)

        rv_schedule.layoutManager = GridLayoutManager(getActivity(), BaseCalendar.DAYS_OF_WEEK)
        rv_schedule.adapter = scheduleRecyclerViewAdapter
        rv_schedule.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL))
        rv_schedule.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL))

        tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }

    fun refreshCurrentMonth(calendar: Calendar) {
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }
}