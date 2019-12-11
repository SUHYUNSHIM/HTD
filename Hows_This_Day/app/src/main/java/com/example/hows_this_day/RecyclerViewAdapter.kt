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
    var heartValue:Int? = null
    val userReference = userDatabase.child(uid)

    init {
        // 날짜 초기화로 리사이클뷰 날짜 구분
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
        //
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        val userListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
               //데이터 읽어오지 못함
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
                        // 연/월/일 로 키값을 읽고 거기에 있는 값을 읽어온다.
                        for (yearSnapshot in dataSnapshot.child("CalendarData").children) {
                            if (yearSnapshot.key!!.toInt() == baseCalendar.calendar.get(Calendar.YEAR)) {
                                val Year = yearSnapshot.key!!.toInt()
                                for (monthSnapshot in yearSnapshot.children) {
                                    if (monthSnapshot.key!!.toInt() - 1 == baseCalendar.calendar.get(Calendar.MONTH)) {
                                        val Month = monthSnapshot.key!!.toInt() -1
                                        for (daySnapshot in monthSnapshot.children) {
                                            if (daySnapshot.key!!.toInt() == baseCalendar.data[position]) {
                                                val Day = daySnapshot.key!!.toInt()
                                                if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                                                    //흑백 제외
                                                } else {
                                                    val heartInt = daySnapshot.getValue(Int::class.java)
                                                    heartInt?.let{
                                                        heartValue = heartInt
                                                        if (heartInt == null){
                                                            //하트 공백
                                                            holder.bt_emptydate.setImageResource(R.drawable.like)
                                                            //    holder.bt_emptydate.setSelected(false)q
                                                        } else if (heartInt == 1){
                                                            // 여자 선택
                                                            holder.bt_emptydate.setImageResource(R.drawable.heart_filled_g)

                                                            //    holder.bt_emptydate.setActivated(true)
                                                        } else if (heartInt ==2){
                                                            // 남자 선택
                                                            holder.bt_emptydate.setImageResource(R.drawable.heart_filled_b)
                                                            // holder.bt_emptydate.setActivated(true)
                                                        } else if (heartInt == 3){
                                                            //풀 하트
                                                            holder.bt_emptydate.setImageResource(R.drawable.redheart)
                                                            //   holder.bt_emptydate.setSelected(true)
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

        holder.bt_emptydate.setOnClickListener() {
            if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                //만약 이번달의 리사이클뷰라 아니라면 클릭이벤트를 무시한다.
            } else {
                //달력정보 데이터베이스 업데이트
                mDate = baseCalendar.data[position]
                mMonth = baseCalendar.calendar.get(Calendar.MONTH) + 1
                mYear = baseCalendar.calendar.get(Calendar.YEAR)
                if (Invited == true) {
                   // 초대자의 경우
                    Log.d("heartvalue",heartValue.toString())
                    if (heartValue == 3) {
                        //풀하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                                .setValue(1)

                    } else  if (heartValue == null){
                        // 공백하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(2)
                    } else if (heartValue == 1){
                      //  오른쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(3)
                    } else if (heartValue == 2){
                        //왼쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(null)
                    }
                    heartValue = null
                } else if (Invited == false){
                    Log.d("heartvalue",heartValue.toString())
                    if (heartValue == 3) {
                        //풀하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(2)

                    } else  if (heartValue == null){
                        // 공백하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(1)
                    } else if (heartValue == 1){
                        //  오른쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(null)
                    } else if (heartValue == 2){
                        //왼쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                            .setValue(3)
                    }
                    heartValue = null
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
}