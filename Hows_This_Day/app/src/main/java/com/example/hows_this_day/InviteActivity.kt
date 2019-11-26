package com.example.hows_this_day

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_invite.*

class InviteActivity : AppCompatActivity() {

    lateinit var name: String
    var RoomName:String? = null
    private var coupleRoom: String? = null
    //나는 초대를 보냈었다.
    private var Invited:Boolean? = null
    val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User");
    val roomDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user!!.uid
    val postReference = mDatabase.child(uid)
    val Listener = object:ValueEventListener{
        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onDataChange(datasnapshot: DataSnapshot) {
            coupleRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
            Invited = datasnapshot.child("Invited").getValue(Boolean::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite)


        //logo 애니메이션 효과
        val iv = findViewById(R.id.imageView_logo) as ImageView
        val anim2 = AnimationUtils.loadAnimation(
            applicationContext, // 현재화면의 제어권자
            R.anim.logo  // 애니메이션 설정 파일
        )
        iv.startAnimation(anim2)

        //회전하는 로고 누르면 DateCountActivity로 넘어감
        iv.setOnClickListener {
            val DCIntent = Intent(this, DateCountActivity::class.java)
            startActivity(DCIntent)
        }

        //custom font
        //커스텀 폰트 적용
        val tv_font2 = findViewById(R.id.date_count_username) as TextView
        tv_font2.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_light.ttf")

        if (intent.hasExtra("UserName")) {
            date_count_username.text = intent.getStringExtra("UserName")
            name = intent.getStringExtra("UserName")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        //초대기능
        val invite = findViewById<Button>(R.id.button3)
        invite.setOnClickListener() {
            DialogInvite()
        }
        //합기능
        val combine = findViewById<Button>(R.id.button4)
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
                    Toast.makeText(this, "방제 입력을 취소했습니다", Toast.LENGTH_SHORT).show()
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


    }

    fun sendMessage(RName: String) {

        val emailIntent = Intent(Intent.ACTION_SEND)

        try {
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))

            emailIntent.type = "text/html"
            emailIntent.setPackage("com.google.android.gm")
            if (emailIntent.resolveActivity(packageManager) != null) {
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 여기어때로 초대합니다.")
                emailIntent.putExtra(Intent.EXTRA_TEXT, "비밀방의 이름이 왔습니다.\n  $RName 를 복사해 주세요")
            }
            startActivity(emailIntent)

        } catch (e: Exception) {
            e.printStackTrace()

            emailIntent.type = "text/html"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("email@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "$name 님께서 당신을 여기어때로 초대합니다.")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "넌 나에게 모욕감을 줬어\n$RName 를 복사해 주세요")
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
                    Toast.makeText(this@InviteActivity, "입력되었습니다", Toast.LENGTH_SHORT).show()
                    var otherName = editText.getText().toString()
                    if (otherName == "") {
                        Toast.makeText(this@InviteActivity, "변수를 입력해 주세요", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@InviteActivity, "그러시든가", Toast.LENGTH_SHORT).show()
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
                        sendMessage("$RoomName")
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
            var  RoomName = editText.getText().toString()
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
                Toast.makeText(this@InviteActivity, "그러시든가", Toast.LENGTH_SHORT).show()
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

    //초대와 초대 되심 버튼을 여기에 옮기기


}
