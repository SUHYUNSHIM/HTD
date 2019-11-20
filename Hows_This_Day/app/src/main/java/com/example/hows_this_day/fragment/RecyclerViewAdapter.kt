package com.example.hows_this_day.fragment

import android.graphics.Color

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hows_this_day.R
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.item_datecell.*
import kotlinx.android.synthetic.main.item_datecell.view.*
import java.util.*
import com.example.hows_this_day.CalendarData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*




class RecyclerViewAdapter(val contextActivity: AFragment) : RecyclerView.Adapter<ViewHolderHelper>() {

    private val baseCalendar = BaseCalendar()
    var mDate : Int = 0
    var mMonth : Int = 0
    var mYear : Int = 0
    var checkNum = 0
    //firebase database

    val mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("UserCalendardata")
    val user = FirebaseAuth.getInstance().currentUser
    val Reference = mDatabase.child(user!!.uid)
    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_datecell, parent, false)
        return ViewHolderHelper(view)

    }


    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        val Listener = object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (calendarSnapshot in dataSnapshot.child("CalendarData").children) {
                    val calendarData = calendarSnapshot.getValue(CalendarData::class.java)
                    calendarData?.let {
                        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {

                        } else {
                            if (calendarData.Day == baseCalendar.data[position]
                                && calendarData.Month - 1 == baseCalendar.calendar.get(Calendar.MONTH)
                                && calendarData.Year == baseCalendar.calendar.get(Calendar.YEAR)
                            ) {
                                holder.bt_emptydate.setSelected(true)
                            }
                        }


                    }
                }
            }
        }
        Reference.addValueEventListener(Listener)
        // if (baseCalendar.data[position].toInt() == fDate
        //   && baseCalendar.calendar.get(Calendar.MONTH) == fMonth-1
        // && baseCalendar.calendar.get(Calendar.YEAR) == fYear){
        //holder.bt_emptyheart_frg.setSelected(true)

        //}

        holder.bt_emptydate.setOnClickListener(){
            mDate= baseCalendar.data[position]
            mMonth = baseCalendar.calendar.get(Calendar.MONTH)+1
            mYear = baseCalendar.calendar.get(Calendar.YEAR)
            Log.d("HeartClick",mDate.toString())
            Log.d("HeartClick2",mMonth.toString())
            databaseUpdate(mYear,mMonth,mDate)
            //  holder.bt_emptyheart_frg.setSelected(true)
            //holder.bt_emptydate.setSelected(true)
            if(checkNum==0){
                holder.bt_emptydate.setActivated(false)
                holder.bt_emptydate.setSelected(true)
                checkNum = 1
            }
            else {
                holder.bt_emptydate.setSelected(false)
                checkNum = 0
            }
            //Log.d("pressed","ddddd")

        }
        //하트를 길게 누를 시 완성된 하트가 되도록 구현 예정 (아직 안됌.)
        holder.bt_emptydate.setOnLongClickListener(){

            holder.bt_emptydate.setActivated(true)
            holder.bt_emptydate.setSelected(true)
            true
        }

        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.tv_date.setTextColor(Color.parseColor("#ff1200"))
        else holder.tv_date.setTextColor(Color.parseColor("#676d6e"))

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.tv_date.alpha = 0.3f
        } else {
            holder.tv_date.alpha = 1f
        }
        holder.tv_date.text = baseCalendar.data[position].toString()
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        contextActivity.refreshCurrentMonth(calendar)
    }
    fun databaseUpdate(
        Year : Int,
        Month : Int,
        Day : Int
    ){
        val User = CalendarData(Year,Month,Day)
        mDatabase.child(user!!.uid).child("CalendarData").push().setValue(User)
    }



}