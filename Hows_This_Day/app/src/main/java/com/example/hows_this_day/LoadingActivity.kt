package com.example.hows_this_day

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_loading.*


class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        setContentView(R.layout.activity_loading)
        startLoading()

        if (intent.hasExtra("UserName")) {
            username_in_loading.text = intent.getStringExtra("UserName")
        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }

        //custom font 적용
        val tv_font2 = findViewById(R.id.textView3) as TextView
        tv_font2.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_regular.ttf")
        val tv_your_name =findViewById(R.id.username_in_loading) as TextView
        tv_your_name.typeface = Typeface.createFromAsset(getAssets(),"fonts/netmarble_bold.ttf")

    }
    private fun startLoading() {
        val handler = Handler()
        val intent = Intent(baseContext, InviteActivity::class.java)
        handler.postDelayed(Runnable {
            val user = FirebaseAuth.getInstance().currentUser       //구글 계정으로 로그인된 사용자의 정보
            user?.let {
                // Name, email address, and profile photo Url
                val name = user.displayName
                intent.putExtra( "UserName",name) // 넘길 intent에 extra넣겠다.
                startActivityForResult(intent, 1)
                finish()
            }

        }, 5000)
    }

}