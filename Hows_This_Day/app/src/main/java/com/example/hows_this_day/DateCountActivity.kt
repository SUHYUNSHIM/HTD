package com.example.hows_this_day

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_datecount.*
import java.util.*


class DateCountActivity : AppCompatActivity() {

    var mYear : Int = 0
    var mMonth: Int = 0
    var mDay  :Int = 0
    //생일 변수
    var bDay : Int = 0
    var bMonth : Int = 0
    var bYear : Int = 0
    // 시작일 변수
    var sDay:Int = 0
    var sMonth:Int = 0
    var sYear:Int = 0
    //애인변수
    var yDay:Int = 0
    var yMonth:Int = 0
    var yYear:Int = 0
    val mDatabase:DatabaseReference = FirebaseDatabase.getInstance().getReference("User");
    val user = FirebaseAuth.getInstance().currentUser
    internal var mTxtDate1: TextView?= null
    internal var mTxtDate2: TextView?= null
    internal var mTxtDate3: TextView?= null
    var postReference = mDatabase.child(user!!.uid)
    val postListener = object:ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            val Start = datasnapshot.child("StartDay").getValue(CalendarData::class.java)
            Start?.let{
                sDay = it.Day
                sMonth = it.Month
                sYear = it.Year
                UpdateNow1()
                heartSelected1()
            }
            val Birth = datasnapshot.child("BirthDay").getValue(CalendarData::class.java)
            Birth?.let{
                bDay = it.Day
                bMonth = it.Month
                bYear = it.Year
                UpdateNow2()
                heartSelected2()
                Log.d("BirthDay is" , "$bYear/$bMonth/$bDay")
            }
            val Your = datasnapshot.child("YourDay").getValue(CalendarData::class.java)
            Your?.let{
                yDay = it.Day
                yMonth = it.Month
                yYear = it.Year
                UpdateNow3()
                heartSelecte3()
            }
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
            databaseUpdate("StartDay",mYear,mMonth,mDay)
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
            databaseUpdate("BirthDay",mYear,mMonth,mDay)

        }

    internal var mDateSetListener3: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            //사용자가 입력한 값을 가져온뒤
            mYear = year
            mMonth = monthOfYear
            mDay = dayOfMonth

            //텍스트뷰의 값을 업데이트함
            UpdateNow3()
            databaseUpdate("YourDay",mYear,mMonth,mDay)

        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datecount)
        //파이어베이스에 날짜정보가 있으면 하트 마크 들어온 상태
        if (sDay != 0){
            val heart1 = findViewById<ImageButton>(R.id.bt_emptyheart)
            heart1.isSelected

        } else{

        }
        if (bDay != 0){
            bt_emptyheart2.setSelected(true)
        } else{

        }
        if (yDay != 0){
            bt_emptyheart3.setSelected(true)
        }




        if (intent.hasExtra("UserName")) {
            date_count_username.text = intent.getStringExtra("UserName")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        //다음화면인 HTDActivity로 넘어가기 위한 버튼, intent
        val b = findViewById<View>(R.id.bt_tohtd) as Button
        b.setOnClickListener(View.OnClickListener {
            val HTDintent = Intent(this, TestActivity::class.java)
            startActivity(HTDintent)
        })




        mTxtDate1 = findViewById<View>(R.id.val_firstdate) as TextView
        mTxtDate2 = findViewById<View>(R.id.val_yourbitrhday) as TextView
        mTxtDate3 = findViewById<View>(R.id.val_loverbithday) as TextView

        //현재 날짜와 시간을 가져오기위한 Calendar 인스턴스 선언
        val cal = GregorianCalendar()
        mYear = cal.get(Calendar.YEAR)
        mMonth = cal.get(Calendar.MONTH)
        mDay = cal.get(Calendar.DAY_OF_MONTH)

        postReference.addValueEventListener(postListener)
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
                } else{
                    DatePickerDialog(this, mDateSetListener1, sYear, sMonth, sDay).show()
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
                    DatePickerDialog(this, mDateSetListener2, bYear, bMonth, bDay).show()
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
                    DatePickerDialog(this, mDateSetListener3, yYear, yMonth, yDay).show()
                }
                //    bt_emptyheart3.setSelected(true)
            }
        }
    }

    //텍스트뷰의 값을 업데이트 하는 메소드

    internal fun UpdateNow1() {
        if (sDay == 0) {
            mTxtDate1?.text = String.format("%d/%d/%d", mYear, mMonth, mDay)
        } else{
            mTxtDate1?.text = String.format("%d/%d/%d", sYear, sMonth, sDay)
        }

    }
    internal fun UpdateNow2() {

        if (bDay == 0) {
            mTxtDate2?.text = String.format("%d/%d/%d", mYear, mMonth, mDay)
        } else {
            mTxtDate2?.text = String.format("%d/%d/%d", bYear, bMonth, bDay)
        }
    }

    // var BirthPost = postReference.child("BirthDay").addValueEventListener(postListener)
    //   Log.d("BirthValue",BirthPost.toString())


    internal fun UpdateNow3() {
        if (yDay == 0) {
            mTxtDate3?.text = String.format("%d/%d/%d", mYear, mMonth, mDay)
        } else {
            mTxtDate3?.text = String.format("%d/%d/%d", yYear, yMonth, yDay)
        }
    }


    fun heartSelected1(){
        if (sDay != 0){
            val heart1 = findViewById<ImageButton>(R.id.bt_emptyheart)
            heart1.setSelected(true)

        } else{

        }
    }
    fun heartSelected2(){
        if (bDay != 0){
            val heart2 = findViewById<ImageButton>(R.id.bt_emptyheart2)
            heart2.setSelected(true)

        } else{

        }
    }
    fun heartSelecte3(){
        if (yDay != 0){
            val heart3 = findViewById<ImageButton>(R.id.bt_emptyheart3)
            heart3.setSelected(true)

        } else{

        }
    }
    fun databaseUpdate(

        DayValue:String,
        Year: Int,
        Month :Int,
        Day: Int
    ) {
        val User = CalendarData(Year,Month+1,Day)
        mDatabase.child(user!!.uid).child(DayValue).setValue(User)
    }
}