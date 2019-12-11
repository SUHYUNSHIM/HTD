package com.example.hows_this_day
/*연인을 초대하거나 초대받은 사람이 방제(couple room name)를 입력하는 Activity
사용자의 이름 표시
invite:   방제 입력 -> 구글 초대 메일 전송 -> 방 생성
invited:  전에 입력된 적이 있는 판단 -> 삭제 후 다시 방제 입력 / 방제 유지 -> 방 참가
*/

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_invite.*

class InviteActivity : AppCompatActivity() {

    var name: String?=null                      //사용자의 이름
    private var coupleRoom: String? = null   //커플룸 이름
    private var Invited:Boolean? = null      //초대를 보낸 여부에 대한 변수

    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")    //User에서 레퍼런스를 가져옴
    val user = FirebaseAuth.getInstance().currentUser                                                 //유저정보를 읽어옴

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)

        //logo 애니메이션 효과
        val iv = findViewById(R.id.imageView_logo) as ImageView
        val anim2 = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.logo
        )
        iv.startAnimation(anim2)

        //회전하는 로고 누르면 DateCountActivity로 넘어감
        iv.setOnClickListener {
            val DCIntent = Intent(this, DateCountActivity::class.java)
            startActivity(DCIntent)
        }

        //custom font
        val tv_font2 = findViewById(R.id.date_count_username) as TextView
        tv_font2.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_light.ttf")

        if (intent.hasExtra("UserName")) {                                  //사용자의 이름 출력
            date_count_username.text = intent.getStringExtra("UserName")
            name = intent.getStringExtra("UserName")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

    /*------------------------[초대 기능 ]------------------------------------------------------------------*/

        //초대를 하는 사람
        val invite = findViewById<Button>(R.id.bt_invite)
        invite.setOnClickListener {
            DialogInvite()
        }

        //초대를 받은 사람
        val combine = findViewById<Button>(R.id.bt_invited)
        combine.setOnClickListener{
            if (Invited == false) {
                combineFun()
            }
            else {

                //초대를 받은 적이 있는 경우,입력 묻는 dialog 띄어줌.
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("이미 초대받은 기록이 있습니다.")
                    .setMessage("기록을 삭제하고 방제를 입력하시겠습니까?")
                    .setIcon(R.mipmap.ic_launcher)

                //초대받는 입장이라고 저장, firebase에 false로 구분
                fun toast_p() {
                    mDatabase.child(user!!.uid).child("Invited").setValue(false)
                    combineFun()
                }

                fun toast_n() {
                    Toast.makeText(this, "방제 입력을 취소했습니다", Toast.LENGTH_SHORT).show()
                }

                //dialog의 예, 아니오 선택
                val dialog_listener = object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE ->
                                toast_p()
                            DialogInterface.BUTTON_NEGATIVE ->
                                toast_n()
                        }
                    }
                }
                dialog.setPositiveButton("네", dialog_listener)
                    .setNegativeButton("아니에요", dialog_listener)
                    .show()
            }
        }
    }
    /*초대 메일 전송 함수 */
    fun sendMessage(RName: String) {

        val emailIntent = Intent(Intent.ACTION_SEND)

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
            emailIntent.type = "text/html"
            emailIntent.setPackage("com.google.android.gm")
            if (emailIntent.resolveActivity(packageManager) != null) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 여기어때로 초대합니다.")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "비밀방의 이름이 왔습니다.\n$RName 를 복사해 주세요")
                startActivity(emailIntent)
            }
            startActivity(emailIntent)

        } catch (e: Exception) {
            e.printStackTrace()

            emailIntent.type = "text/html"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 이날어때로 초대합니다.")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "넌 나에게 모욕감을 줬어. \n$RName 를 복사해 주세요")
            startActivity(Intent.createChooser(emailIntent, "Send Email"))
        }
    }

    fun DialogCombine() {
        //받은 변수 입력하는 다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("타이틀은 글쎄")
            .setMessage("받은 변수를 붙여주십시오.")

        val editText = EditText(this)
        //엔터 누르면 입력받음
        val editListener:DialogInterface.OnKeyListener = object : DialogInterface.OnKeyListener {
            override fun onKey(Dialog: DialogInterface?, num: Int, event: KeyEvent?): Boolean {
                //event? event!!
                if (event?.action == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(this@InviteActivity, "입력되었습니다", Toast.LENGTH_SHORT).show()
                    val otherName = editText.getText().toString()
                    if (otherName == "") {
                        Toast.makeText(this@InviteActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                        DialogCombine()
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
            val otherName = editText.getText().toString()
            if (otherName == "") {
                Toast.makeText(this, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                DialogCombine()
                // mDatabase.child(user!!.uid).child("CoupleName").setValue(null)
            } else {
                mDatabase.child(user!!.uid).child("CoupleRoom").setValue(otherName)
            }
        }

        fun toast_n() {
            //취소
        }
        val dialog_listener = object : DialogInterface.OnClickListener {
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
        //초대 과정에서 다이얼로그 생성
        if (coupleRoom == null) {
            mDatabase.child(user!!.uid).child("Invited").setValue(true)
            RoomSend()

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("초대하기")
                .setMessage("다시 초대하시겠습니까?")
                .setIcon(R.mipmap.ic_launcher)

            fun toast_p() {
                mDatabase.child(user!!.uid).child("Invited").setValue(true)
                RoomSend()
            }

            fun toast_n() {
                Toast.makeText(this@InviteActivity, "초대를 취소하였습니다.", Toast.LENGTH_SHORT).show()
            }

            val dialog_listener = object : DialogInterface.OnClickListener {
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

    /*방 생성, 방제 전달 함수 */
    fun RoomSend(){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("방제 전달")
            .setMessage("방제를 상대에게 전하시겠습니까?")

        val editText:EditText = EditText(this)

        val editListener:DialogInterface.OnKeyListener = object : DialogInterface.OnKeyListener {
            override fun onKey(Dialog: DialogInterface?, num: Int, event: KeyEvent?): Boolean {
                //event? event!!
                if (event?.action == KeyEvent.KEYCODE_ENTER) {
                    var  RoomName = editText.getText().toString()
                    Toast.makeText(this@InviteActivity, "입력되었습니다", Toast.LENGTH_SHORT).show()
                    RoomName = editText.getText().toString()
                    if (RoomName == "") {
                        Toast.makeText(this@InviteActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                        DialogInvite()
                        //   mDatabase.child(user!!.uid).child("CoupleName").setValue(null)
                    } else {
                        mDatabase.child(user!!.uid).child("CoupleRoom").setValue(RoomName)
                        sendMessage("RoomName")
                    }
                    return true
                }
                return false
            }
        }
        dialog.setOnKeyListener(editListener)
        dialog.setView(editText)
        fun toast_p() {
            //입력
            val  RoomName = editText.getText().toString()
            if (RoomName == null) {
                Toast.makeText(this@InviteActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
                DialogInvite()
                //   mDatabase.child(user!!.uid).child("CoupleName").setValue(null)
            } else {
                mDatabase.child(user!!.uid).child("CoupleRoom").setValue(RoomName)
                sendMessage("$RoomName")
            }
        }

        fun toast_n() {
            //cancel
        }

        val dialog_listener = object : DialogInterface.OnClickListener {
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
       //변수 입력하는 함수
        if (coupleRoom == null) {
            DialogCombine()
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("변수입력")
                .setMessage("다시 입력하시겠습니까?")
                .setIcon(R.mipmap.ic_launcher)

            fun toast_p() {
                DialogCombine()
            }

            fun toast_n() {
                Toast.makeText(this@InviteActivity, "그러시든가", Toast.LENGTH_SHORT).show()
            }

            val dialog_listener = object : DialogInterface.OnClickListener {
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
