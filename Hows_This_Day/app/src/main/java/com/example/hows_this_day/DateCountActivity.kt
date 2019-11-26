package com.example.hows_this_day

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_datecount.*
import java.util.*
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T

class DateCountActivity : AppCompatActivity() {

    lateinit var name: String
    private var coupleRoom: String? = null
    //나는 초대를 보냈었다.
    private var Invited:Boolean? = false
    private var nameRoom:String? = null
   // private var coupleName: String? = null
    var mYear: Int = 0
    var mMonth: Int = 0
    var mDay: Int = 0
    //생일 변수
    var bDay: Int = 0
    var bMonth: Int = 0
    var bYear: Int = 0
    // 시작일 변수
    var sDay: Int = 0
    var sMonth: Int = 0
    var sYear: Int = 0
    //애인변수
    var yDay: Int = 0
    var yMonth: Int = 0
    var yYear: Int = 0
    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User");
    val roomDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid
    val postReference = mDatabase.child(uid)
    internal var mTxtDate1: TextView? = null
    internal var mTxtDate2: TextView? = null
    internal var mTxtDate3: TextView? = null

    val BigListener = object:ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(datasnapshot: DataSnapshot) {


            val postListener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    val Start = datasnapshot.child("StartDay").getValue(CalendarData::class.java)
                    Start?.let {
                        sDay = it.Day
                        sMonth = it.Month
                        sYear = it.Year
                        UpdateNow1()
                        heartSelected1()
                    }
                    val Birth = datasnapshot.child("BirthDay").getValue(CalendarData::class.java)
                    Birth?.let {
                        bDay = it.Day
                        bMonth = it.Month
                        bYear = it.Year
                        UpdateNow2()
                        heartSelected2()
                        Log.d("BirthDay is", "$bYear/$bMonth/$bDay")
                    }
                    val Your = datasnapshot.child("YourDay").getValue(CalendarData::class.java)
                    Your?.let {
                        yDay = it.Day
                        yMonth = it.Month
                        yYear = it.Year
                        UpdateNow3()
                        heartSelecte3()
                    }
                }
            }
            coupleRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
            Invited = datasnapshot.child("Invited").getValue(Boolean::class.java)
            val roomReference = coupleRoom?.let{roomDatabase.child(it!!)}
            roomReference?.addValueEventListener(postListener)

        }



    }

    //날짜 대화상자 리스너 부분
    internal var mDateSetListener1: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            //사용자가 입력한 값을 가져온뒤
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

            //텍스트뷰의 값을 업데이트함
            UpdateNow1()
            databaseUpdate("StartDay", mYear, mMonth, mDay)
        }
    internal var mDateSetListener2: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            //사용자가 입력한 값을 가져온뒤
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

            //텍스트뷰의 값을 업데이트함
            UpdateNow2()
            //데이터베이스 업데이트
            databaseUpdate("BirthDay", mYear, mMonth, mDay)

        }

    internal var mDateSetListener3: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            //사용자가 입력한 값을 가져온뒤
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

            //텍스트뷰의 값을 업데이트함
            UpdateNow3()
            databaseUpdate("YourDay", mYear, mMonth, mDay)

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datecount)

        //custom font
        //font 바꾸는 부분을 구조화할 필요가 있다.ㅎㅎ
        val tv_first_date = findViewById(R.id.tv_firstdate) as TextView?
        tv_first_date?.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_regular.ttf")
        val tv_your_birthday = findViewById(R.id.tv_yourbirthday) as TextView?
        tv_your_birthday?.typeface =Typeface.createFromAsset(getAssets(),"fonts/netmarble_regular.ttf")
        val tv_lover_birthday = findViewById(R.id.tv_loverbithday) as TextView?
        tv_lover_birthday?.typeface = Typeface.createFromAsset(getAssets(),"fonts/netmarble_regular.ttf")
        val val_first_date = findViewById(R.id.val_firstdate) as TextView?
        val_first_date?.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_light.ttf")
        val val_your_birthday = findViewById(R.id.val_yourbitrhday) as TextView?
        val_your_birthday?.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_light.ttf")
        val val_lover_birthday = findViewById(R.id.val_loverbithday) as TextView?
        val_lover_birthday?.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_light.ttf")





        //실험. 공유기능


        //파이어베이스에 날짜정보가 있으면 하트 마크 들어온 상태
        if (sDay != 0) {
            val heart1 = findViewById<ImageButton>(R.id.bt_emptyheart)
            heart1.isSelected

        } else {

        }
        if (bDay != 0) {
            bt_emptyheart2.setSelected(true)
        } else {

        }
        if (yDay != 0) {
            bt_emptyheart3.setSelected(true)
        }



        //다음화면인 HTDActivity로 넘어가기 위한 버튼, intent
        val b = findViewById<View>(R.id.bt_tohtd) as Button
        b.setOnClickListener(View.OnClickListener {
            val HTDintent = Intent(this, TabActivity::class.java)
            startActivity(HTDintent)
        })
        //초대기능

        //합기능



        mTxtDate1 = findViewById<View>(R.id.val_firstdate) as TextView
        mTxtDate2 = findViewById<View>(R.id.val_yourbitrhday) as TextView
        mTxtDate3 = findViewById<View>(R.id.val_loverbithday) as TextView

        //현재 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언
        val cal = GregorianCalendar()
        mYear = cal.get(Calendar.YEAR)
        mMonth = cal.get(Calendar.MONTH)
        mDay = cal.get(Calendar.DAY_OF_MONTH)

        postReference.addValueEventListener(BigListener)
        UpdateNow1()//화면에 텍스트뷰에 업데이트 해줌.
        UpdateNow2()
        UpdateNow3()


        //    var BirthPost = postReference.child("BirthDay").addValueEventListener(postListener)
        //  Log.d("BirthValue",BirthPost.toString())
    }

    fun mOnClick(v: View) {

        when (v.id) {

            //하트 버튼이 눌리면 대화상자를 보여줌

            R.id.bt_emptyheart -> {
                //여기서 리스너도 등록함
                if (sDay == 0) {
                    val cal = GregorianCalendar()

                    mYear = cal.get(Calendar.YEAR)
                    mMonth = cal.get(Calendar.MONTH)
                    mDay = cal.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(this, mDateSetListener1, mYear, mMonth, mDay).show()
                } else {
                    DatePickerDialog(this, mDateSetListener1, sYear, sMonth-1, sDay).show()
                }
                //     bt_emptyheart.setSelected(true)
            }

            R.id.bt_emptyheart2 -> {
                if (bDay == 0) {
                    val cal = GregorianCalendar()

                    mYear = cal.get(Calendar.YEAR)
                    mMonth = cal.get(Calendar.MONTH)
                    mDay = cal.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(this, mDateSetListener2, mYear, mMonth, mDay).show()
                } else {
                    DatePickerDialog(this, mDateSetListener2, bYear, bMonth -1, bDay).show()
                }
                //   bt_emptyheart2.setSelected(true)

            }
            R.id.bt_emptyheart3 -> {
                if (yDay == 0) {
                    val cal = GregorianCalendar()

                    mYear = cal.get(Calendar.YEAR)
                    mMonth = cal.get(Calendar.MONTH)
                    mDay = cal.get(Calendar.DAY_OF_MONTH)
                    DatePickerDialog(this, mDateSetListener3, mYear, mMonth, mDay).show()
                } else {
                    DatePickerDialog(this, mDateSetListener3, yYear, yMonth -1, yDay).show()
                }
            }

        }
    }

    //텍스트뷰의 값을 업데이트 하는 메소드

    internal fun UpdateNow1() {
        if (sDay == 0) {
            mTxtDate1?.text = String.format("%d/%d/%d", mYear, mMonth+1, mDay)
        } else {
            mTxtDate1?.text = String.format("%d/%d/%d", sYear, sMonth, sDay)
        }

    }

    internal fun UpdateNow2() {

        if (bDay == 0) {
            mTxtDate2?.text = String.format("%d/%d/%d", mYear, mMonth+1, mDay)
        } else {
            mTxtDate2?.text = String.format("%d/%d/%d", bYear, bMonth, bDay)
        }
    }

    // var BirthPost = postReference.child("BirthDay").addValueEventListener(postListener)
    //   Log.d("BirthValue",BirthPost.toString())


    internal fun UpdateNow3() {
        if (yDay == 0) {
            mTxtDate3?.text = String.format("%d/%d/%d", mYear, mMonth+1, mDay)
        } else {
            mTxtDate3?.text = String.format("%d/%d/%d", yYear, yMonth, yDay)
        }
    }


    fun heartSelected1() {
        if (sDay != 0) {
            val heart1 = findViewById<ImageButton>(R.id.bt_emptyheart)
            heart1.setSelected(true)

        } else {

        }
    }

    fun heartSelected2() {
        if (bDay != 0) {
            val heart2 = findViewById<ImageButton>(R.id.bt_emptyheart2)
            heart2.setSelected(true)

        } else {

        }
    }

    fun heartSelecte3() {
        if (yDay != 0) {
            val heart3 = findViewById<ImageButton>(R.id.bt_emptyheart3)
            heart3.setSelected(true)

        } else {

        }
    }

    fun databaseUpdate(

        DayValue: String,
        Year: Int,
        Month: Int,
        Day: Int
    ) {
        val roomListener  = object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(datasnapshot: DataSnapshot) {
                Log.d("OnDataChange","$nameRoom")
                nameRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
                val User = CalendarData(Year, Month + 1, Day)
                nameRoom?.let{roomDatabase.child(it!!).child(DayValue).setValue(User)}
            }
        }
        postReference!!.addValueEventListener(roomListener)
    }









}