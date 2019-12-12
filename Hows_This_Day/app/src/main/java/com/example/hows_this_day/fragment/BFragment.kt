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
import java.text.SimpleDateFormat
import java.util.*

class BFragment : Fragment() {

    lateinit var scheduleRecyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_b, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        //뷰 설정g
    }

    fun initView() {
        //리사이클러 뷰를 이용해 프래그먼트에 달력
        scheduleRecyclerViewAdapter = RecyclerViewAdapter(this)

        rv_schedule.layoutManager = GridLayoutManager(getActivity(), BaseCalendar.DAYS_OF_WEEK)
        rv_schedule.adapter = scheduleRecyclerViewAdapter
        rv_schedule.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL))
        rv_schedule.addItemDecoration(DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL))

        tv_prev_month.setOnClickListener {
            //이전달력이동
            scheduleRecyclerViewAdapter.changeToPrevMonth()
        }

        tv_next_month.setOnClickListener {
            //다음달력이동
            scheduleRecyclerViewAdapter.changeToNextMonth()
        }
    }
    fun refreshCurrentMonth(calendar: Calendar) {
        //달력 초기화
        val sdf = SimpleDateFormat("yyyy MM", Locale.KOREAN)
        tv_current_month.text = sdf.format(calendar.time)
    }
}