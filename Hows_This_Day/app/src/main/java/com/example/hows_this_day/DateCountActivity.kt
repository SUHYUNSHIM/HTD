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
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid
    internal var mTxtDate1: TextView? = null
    internal var mTxtDate2: TextView? = null
    internal var mTxtDate3: TextView? = null
    var postReference = mDatabase.child(user!!.uid)
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
            coupleRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
            Invited = datasnapshot.child("Invited").getValue(Boolean::class.java)

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




        if (intent.hasExtra("UserName")) {
            date_count_username.text = intent.getStringExtra("UserName")
            name = intent.getStringExtra("UserName")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        //다음화면인 HTDActivity로 넘어가기 위한 버튼, intent
        val b = findViewById<View>(R.id.bt_tohtd) as Button
        b.setOnClickListener(View.OnClickListener {
            val HTDintent = Intent(this, TabActivity::class.java)
            startActivity(HTDintent)
        })
        //초대기능
        val invite = findViewById<Button>(R.id.button_invite)
        invite.setOnClickListener() {
            DialogInvite()
        }
        //합기능
        val combine = findViewById<Button>(R.id.button_combine)
        combine.setOnClickListener() {
            if (Invited == false) {
                combineFun()
            }
            else {
                var dialog = AlertDialog.Builder(this)
                dialog.setTitle("이미 초대하신 기록이 있습니다.")
                    .setMessage("초대한 기록을 삭제하고 메세지를 입력하시겠습니까?")
                    .setIcon(R.mipmap.ic_launcher)

                fun toast_p() {
                    mDatabase.child(user!!.uid).child("Invited").setValue(false)
                    combineFun()
                    //  mDatabase.child(user!!.uid).child("CoupleRoom").setValue()
                }

                fun toast_n() {
                    Toast.makeText(this@DateCountActivity, "방제 입력을 취소했습니다", Toast.LENGTH_SHORT).show()
                }

                var dialog_listener = object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE ->
                                toast_p()
                            DialogInterface.BUTTON_NEGATIVE ->
                                toast_n()
                        }
                    }
                }
                dialog.setPositiveButton("넹", dialog_listener)
                    .setNegativeButton("아닌데요?", dialog_listener)
                    .show()

            }
        }


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
                } else {
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
        val User = CalendarData(Year, Month + 1, Day)
        mDatabase.child(uid).child(DayValue).setValue(User)
    }

    fun sendMessage() {

        val emailIntent = Intent(Intent.ACTION_SEND)

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))

            emailIntent.type = "text/html"
            emailIntent.setPackage("com.google.android.gm")
            if (emailIntent.resolveActivity(packageManager) != null) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 여기어때로 초대합니다.")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "비밀방의 이름이 왔습니다.\n  $uid 를 복사해 주세요")
            }
                startActivity(emailIntent)

        } catch (e: Exception) {
            e.printStackTrace()

            emailIntent.type = "text/html"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 여기어때로 초대합니다.")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "넌 나에게 모욕감을 줬어\n$uid 를 복사해 주세요")
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }



    }

    fun DialogCombine() {
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("타이틀은 글쎄")
            .setMessage("받은 변수를 붙여주십시오.")

        val editText: EditText = EditText(this)
        //엔터 누르면 입력받음
        val editListener:DialogInterface.OnKeyListener = object : DialogInterface.OnKeyListener {
            override fun onKey(Dialog: DialogInterface?, num: Int, event: KeyEvent?): Boolean {
                //event? event!!
                if (event?.action == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(this@DateCountActivity, "입력되었습니다", Toast.LENGTH_SHORT).show()
                     var otherName = editText.getText().toString()
                    if (otherName == "") {
                        Toast.makeText(this@DateCountActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                        DialogCombine()
                     //   mDatabase.child(user!!.uid).child("CoupleName").setValue(null)
                    } else {
                        mDatabase.child(user!!.uid).child("CoupleRoom").setValue(otherName)

                    }
                    return true
                }
                return false
            }
        }
        dialog.setOnKeyListener(editListener)
            .setView(editText)
        fun toast_p() {
            //입력
            var otherName = editText.getText().toString()
            if (otherName == "") {
                Toast.makeText(this@DateCountActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                DialogCombine()
               // mDatabase.child(user!!.uid).child("CoupleName").setValue(null)
            } else {
                mDatabase.child(user!!.uid).child("CoupleRoom").setValue(otherName)
            }
        }

        fun toast_n() {
            //취소
        }

        var dialog_listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        toast_p()
                    DialogInterface.BUTTON_NEGATIVE ->
                        toast_n()
                }
            }
        }
        dialog.setPositiveButton("입력", dialog_listener)
            .setNegativeButton("취소", dialog_listener)
            .show()
    }

    fun DialogInvite() {
        if (coupleRoom == null) {
            mDatabase.child(user!!.uid).child("Invited").setValue(true)
            RoomSend()

          //  sendMessage()

        } else {
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("초대하기")
                .setMessage("다시 초대하시겠습니까?")
                .setIcon(R.mipmap.ic_launcher)

            fun toast_p() {
                mDatabase.child(user!!.uid).child("Invited").setValue(true)
                RoomSend()
              //  mDatabase.child(user!!.uid).child("CoupleRoom").setValue()
            }

            fun toast_n() {
                Toast.makeText(this@DateCountActivity, "그러시든가", Toast.LENGTH_SHORT).show()
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                        DialogInterface.BUTTON_NEGATIVE ->
                            toast_n()
                    }
                }
            }
            dialog.setPositiveButton("넹", dialog_listener)
                .setNegativeButton("아닌데요?", dialog_listener)
                .show()


        }
    }
    //초대 메일 보내기
    fun RoomSend(){
        var dialog = AlertDialog.Builder(this)
        dialog.setTitle("방제 전달")
            .setMessage("방제를 상대에게 전하시겠습니까?")

        fun toast_p() {
            //입력

                mDatabase.child(user!!.uid).child("CoupleRoom").setValue(uid)
                sendMessage()

        }

        fun toast_n() {
            //cancel
        }

        var dialog_listener = object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                when (which) {
                    DialogInterface.BUTTON_POSITIVE ->
                        toast_p()
                    DialogInterface.BUTTON_NEGATIVE ->
                        toast_n()
                }
            }
        }
        dialog.setPositiveButton("입력", dialog_listener)
            .setNegativeButton("취소", dialog_listener)
            .show()

    }
    fun combineFun(){
        if (coupleRoom == null) {
            DialogCombine()
        } else {
            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("변수입력")
                .setMessage("다시 입력하시겠습니까?")
                .setIcon(R.mipmap.ic_launcher)

            fun toast_p() {
                DialogCombine()
            }

            fun toast_n() {
                Toast.makeText(this@DateCountActivity, "그러시든가", Toast.LENGTH_SHORT).show()
            }

            var dialog_listener = object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE ->
                            toast_p()
                        DialogInterface.BUTTON_NEGATIVE ->
                            toast_n()
                    }
                }
            }
            dialog.setPositiveButton("넹", dialog_listener)
                .setNegativeButton("아니요?", dialog_listener)
                .show()

        }
    }
}