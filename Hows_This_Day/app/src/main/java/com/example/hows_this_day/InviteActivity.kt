package com.example.hows_this_day

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_invite.*

class InviteActivity : AppCompatActivity() {

    lateinit var name: String

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

    }

    //화면 전환 버튼


    //초대와 초대 되심 버튼을 여기에 옮기기
    //DateCountActivity

}
