package com.example.hows_this_day
//출처:https://woochan-dev.tistory.com/27
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
    var heartValue:Int? = 0
    //firebase database
    var Invited:Boolean?= null
    var mReference:DatabaseReference? =null
    var roomName:String? = null
    //파이어베이스 데이터베이스 저장장소(키 값)
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
    private val userDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
    private val user = FirebaseAuth.getInstance().currentUser
    private val uid = user!!.uid
    private val userReference = userDatabase.child(uid)

    init {
        // 날짜 초기화로 리사이클뷰 날짜 구분
        baseCalendar.initBaseCalendar {
            refreshView(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        //화면에 표시될 아이템 뷰 설정
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_datecell, parent, false)
        return ViewHolderHelper(view)

    }


    override fun getItemCount(): Int {
        //전체 아이템 갯수 리턴
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        //position에 해당하는 데이터를 뷰홀데 아이템에 표시
        val userListener = object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
               //데이터 읽어오지 못함
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                //유저정보에서 초대유무, 방제를 읽어와 룸 데이터에 방의 정보를 읽어온다.
                Invited = datasnapshot.child("Invited").getValue(Boolean::class.java)
                roomName = datasnapshot.child("CoupleRoom").getValue(String::class.java)
                mReference = roomName?.let { mDatabase.child(it) }
                val Listener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // 연/월/일 로 키값을 읽고 거기에 있는 값을 읽어온다.
                        for (yearSnapshot in dataSnapshot.child("CalendarData").children) {
                            if (yearSnapshot.key!!.toInt() == baseCalendar.calendar.get(Calendar.YEAR)) {
                                for (monthSnapshot in yearSnapshot.children) {
                                    if (monthSnapshot.key!!.toInt() - 1 == baseCalendar.calendar.get(Calendar.MONTH)) {
                                        for (daySnapshot in monthSnapshot.children) {
                                            if (daySnapshot.key!!.toInt() == baseCalendar.data[position]) {
                                                if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                                                    //흑백 제외
                                                } else {
                                                    val heartInt = daySnapshot.getValue(Int::class.java)
                                                    heartInt?.let{
                                                        if (heartInt == 0){
                                                            //하트 공백
                                                            //데이터베이스의 데이터를 지운다.
                                                            holder.bt_emptydate.setImageResource(R.drawable.like)
                                                            mDatabase.child("$roomName").child("CalendarData").child("$mYear/$mMonth/$mDate")
                                                                .setValue(null)
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
                                                        } else{

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
                //m레퍼런스에서 데이터를 읽어온다.
                mReference?.addValueEventListener(Listener)
            }
        }
        //유저 레퍼런스에서 데이터를 읽어온다.
        userReference.addValueEventListener(userListener)
        //하트버튼 클릭 이벤트
        holder.bt_emptydate.setOnClickListener() {
            // 변수에 오늘의 날짜 정보 저장
            mDate = baseCalendar.data[position]
            mMonth = baseCalendar.calendar.get(Calendar.MONTH) + 1
            mYear = baseCalendar.calendar.get(Calendar.YEAR)
            val buttonListener = object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                   val Value:Int?= datasnapshot.child("CalendarData").child("$mYear/$mMonth/$mDate").getValue(Int::class.java)

                        Value?.let{
                                heartValue = Value
                        }
                }
            }

            Log.d("mreference",mReference.toString())
            if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
                //만약 이번달의 리사이클뷰라 아니라면 클릭이벤트를 무시한다.
            } else {
                if (Invited == true) {
                    // 초대자의 경우
                    mReference?.addValueEventListener(buttonListener)
                    if (heartValue == 3) {
                        //풀하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(1)

                    } else if (heartValue == 1) {
                        //  오른쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(3)
                    } else if (heartValue == 2) {
                        //왼쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(0)
                    } else {
                        // 공백하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(2)
                    }
                } else if (Invited == false) {
                    mReference?.addValueEventListener(buttonListener)
                    Log.d("heartvalue", heartValue.toString())
                    if (heartValue == 3) {
                        //풀하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(2)
                    } else if (heartValue == 1) {
                        //  오른쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(0)
                    } else if (heartValue == 2) {
                        //왼쪽하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(3)
                    } else {
                        // 공백하트 클릭
                        mDatabase.child("$roomName").child("CalendarData")
                            .child("$mYear/$mMonth/$mDate")
                            .setValue(1)
                    }
                } else {
                    Log.d("데이터가 없습니다", Invited.toString())
                }
            }
        }
        //이번달 아닌 날짜 색 바꿈

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
    //월 바꾸는 함수
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