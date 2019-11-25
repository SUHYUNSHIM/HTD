package com.example.hows_this_day

import android.graphics.Color

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.item_datecell.*
import java.util.*

import com.example.hows_this_day.fragment.BFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*




class RecyclerViewAdapter(val contextActivity: BFragment) : RecyclerView.Adapter<ViewHolderHelper>() {

    private val baseCalendar = BaseCalendar()
    var mDate: Int = 0
    var mMonth: Int = 0
    var mYear: Int = 0
    //firebase database
    var Invited:Boolean?= null
    var roomName:String? = null
    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
    val userDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid



  //  var mReference:DatabaseReference = mDatabase.child(roomName!!)
    val userReference = userDatabase.child(uid)

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_datecell, parent, false)
        return ViewHolderHelper(view)

    }


    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        val userListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                Invited = datasnapshot.child("Invited").getValue(Boolean::class.java)
                roomName = datasnapshot.child("CoupleRoom").getValue(String::class.java)
                val mReference = roomName?.let { mDatabase.child(it) }
                val Listener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (yearSnapshot in dataSnapshot.child("CalendarData").children) {
                            if (yearSnapshot.key!!.toInt() == baseCalendar.calendar.get(Calendar.YEAR)) {
                                for (monthSnapshot in yearSnapshot.children) {
                                    if (monthSnapshot.key!!.toInt() - 1 == baseCalendar.calendar.get(
                                            Calendar.MONTH
                                        )
                                    ) {
                                        for (daySnapshot in monthSnapshot.children) {
                                            if (daySnapshot.key!!.toInt() == baseCalendar.data[position]) {
                                                if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                                                    //흑백 제외
                                                } else {
                                                    val heartBoolean = daySnapshot.getValue(CoupleData::class.java)
                                                    heartBoolean?.let{
                                                        val MH = it.maleHeart
                                                        val FH = it.femaleHeart
                                                            if (MH == true && FH == true){
                                                                //둘다 선택
                                                                holder.bt_emptydate.setSelected(true)
                                                                holder.bt_emptydate.setPressed(true)
                                                            } else if (MH == false && FH == true){
                                                                // 여자 선택
                                                                holder.bt_emptydate.setSelected(false)
                                                                holder.bt_emptydate.setPressed(true)
                                                            } else if (MH == true && FH == false){
                                                                // 남자 선택
                                                                holder.bt_emptydate.setSelected(true)
                                                                holder.bt_emptydate.setPressed(false)
                                                            } else if (MH == false && FH == false){
                                                                //선택 x
                                                                holder.bt_emptydate.setSelected(false)
                                                                holder.bt_emptydate.setPressed(false)
                                                            }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                mReference?.addValueEventListener(Listener)
            }
        }


        userReference.addValueEventListener(userListener)
        // if (baseCalendar.data[position].toInt() == fDate
        //   && baseCalendar.calendar.get(Calendar.MONTH) == fMonth-1
        // && baseCalendar.calendar.get(Calendar.YEAR) == fYear){
        //holder.bt_emptyheart_frg.setSelected(true)

        //}

        holder.bt_emptydate.setOnClickListener() {
            if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {

            } else {
                //달력정보 데이터베이스 업데이트
                mDate = baseCalendar.data[position]
                mMonth = baseCalendar.calendar.get(Calendar.MONTH) + 1
                mYear = baseCalendar.calendar.get(Calendar.YEAR)
                Log.d("HeartClick", mDate.toString())
                Log.d("HeartClick2", mMonth.toString())
                if (Invited == true) {
                    if (holder.bt_emptydate.isSelected == true) {
                        databaseDelete(mYear, mMonth, mDate)
                    } else {
                        databaseUpdate(mYear, mMonth, mDate)
                    }
                } else{
                    if (holder.bt_emptydate.isPressed == true){
                        databaseDelete(mYear,mMonth,mDate)
                    } else{
                        databaseUpdate(mYear, mMonth, mDate)
                    }

                }
            }
        }


        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.tv_date.setTextColor(
            Color.parseColor(
                "#ff1200"
            )
        )
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
        Year: Int,
        Month: Int,
        Day: Int
    ) {

        if (Invited == true) {
            val User = CoupleData(maleHeart = true)
            mDatabase.child(roomName!!).child("CalendarData").child("$Year/$Month/$Day")
                .setValue(User)
        } else {
            val User = CoupleData(femaleHeart = true)
            mDatabase.child(roomName!!).child("CalendarData").child("$Year/$Month/$Day")
                .setValue(User)
        }

    }

    fun databaseDelete(
        Year: Int,
        Month: Int,
        Day: Int
    ) {
        if (Invited == true) {
            val User = CoupleData(maleHeart =false)
            mDatabase.child("$roomName").child("CalendarData").child("$Year/$Month/$Day")
                .setValue(User)
        } else{
            val User = CoupleData(femaleHeart = false)
            mDatabase.child("$roomName").child("CalendarData").child("$Year/$Month/$Day")
                .setValue(User)
        }
    }


}