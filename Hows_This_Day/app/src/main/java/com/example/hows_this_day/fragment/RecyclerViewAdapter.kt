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

import com.example.hows_this_day.CoupleData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*




class RecyclerViewAdapter(val contextActivity: AFragment) : RecyclerView.Adapter<ViewHolderHelper>() {

    private val baseCalendar = BaseCalendar()
    var mDate : Int = 0
    var mMonth : Int = 0
    var mYear : Int = 0
    //firebase database

    val mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
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
                for (yearSnapshot in dataSnapshot.child("CalendarData").children) {
                   if( yearSnapshot.key!!.toInt() == baseCalendar.calendar.get(Calendar.YEAR)){
                       for (monthSnapshot in yearSnapshot.children){
                           if (monthSnapshot.key!!.toInt() -1 == baseCalendar.calendar.get(Calendar.MONTH)){
                               for (daySnapshot in monthSnapshot.children){
                                   if (daySnapshot.key!!.toInt() == baseCalendar.data[position]){
                                       if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate){
                                           //흑백 제외
                                       } else{
                                           holder.bt_emptydate.setSelected(true)
                                           holder.bt_emptydate.setPressed(true)
                                       }
                                   }
                               }
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
            if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {

            } else{
                //달력정보 데이터베이스 업데이트
                mDate = baseCalendar.data[position]
                mMonth = baseCalendar.calendar.get(Calendar.MONTH) + 1
                mYear = baseCalendar.calendar.get(Calendar.YEAR)
                Log.d("HeartClick", mDate.toString())
                Log.d("HeartClick2", mMonth.toString())
                if(holder.bt_emptydate.isSelected == true){
                    databaseDelete(mYear,mMonth,mDate)
                } else {
                    databaseUpdate(mYear, mMonth, mDate)
                }
            }
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
        val User = CoupleData(false)
        mDatabase.child(user!!.uid).child("CalendarData").child("$Year/$Month/$Day").setValue(User)
    }
    fun databaseDelete(
        Year:Int,
        Month:Int,
        Day:Int
    ){
        mDatabase.child(user!!.uid).child("CalendarData").child("$Year/$Month/$Day").setValue(null)
    }


}