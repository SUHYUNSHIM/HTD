package com.example.hows_this_day
/**화면 진행 순서 MainActivity -> LoadingActivity-> InviteActivity-> DateCountActivity-> HTDActivity

Firebase에 사용자 정보 넘겨주는 Activity
구글 로그인 ->  구글 사용자의 id 토큰을 받음 -> firebase에 계정 인증 완료
 **/
import android.content.Intent
import android.os.Bundle;
import android.view.animation.AnimationUtils
import android.widget.ImageView


import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity


import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import android.graphics.Typeface
import android.widget.TextView


class MainActivity :AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener {
    private var mGoogleApiClient: GoogleApiClient? = null       //GoogleApiClient 객체
    private var mAuth: FirebaseAuth? = null                      //firebsae authentication 객체


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        //cupid 애니메이션 효과
        val iv = findViewById(R.id.cupid) as ImageView
        val anim = AnimationUtils.loadAnimation(
            applicationContext, // 현재화면의 제어권자
            R.anim.cupid       // 애니메이션 설정 파일
        )
        iv.startAnimation(anim)

        //커스텀 폰트 적용
        val tv_font = findViewById(R.id.textView) as TextView
        tv_font.typeface = Typeface.createFromAsset(getAssets(), "fonts/netmarble_bold.ttf")


     /*------------------------[구글 로그인 인증 ]-----------------------------------------------------------*/

        // 구글 로그인 확인.
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        //구글로그인 버튼에 대한 이벤트
        val button = findViewById(R.id.login_button) as SignInButton

        //이벤트 발생했을때, 구글로그인 버튼에 대한 (구글정보를 인텐트로 넘기는 값)
        //로그인한다고 하는사람이 구글 사용자인지 물어본다.
        button.setOnClickListener {

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)     //성공했음에 대한 result 상수 전달
        }
    }

    /*Intent Result값 반환되는 로직 */
    override fun  onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 구글로그인 버튼에 대한 응답.
         if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {

                //구글버튼 로그인 누르고, 구글사용자 확인되면 실행되는 로직
                //구글 로그인이 성공적이라면, firebase로 로그인 인증이 완료.
                val account = result.signInAccount
                firebaseAuthWithGoogle(account!!)               //구글이용자 확인된 사람정보 파이어베이스로 넘기기(함수호출)
                                                                 //!! ->  account가 null인 경우 NullPointerException을 발생시킨다.
            }
            else {
                Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*파이어베이스로 값넘기는 함수 */
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        //파이어베이스로 받은 구글사용자가 확인된 이용자의 값을 토큰으로 받고
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->

                //로그인이 성공하면, 로그인된 사용자의 정보를 업데이트 한다.
                if (task.isSuccessful) {

                    Toast.makeText(this@MainActivity, "아이디 생성완료", Toast.LENGTH_SHORT).show()
                    val loginIntent = Intent(this, LoadingActivity::class.java)     //새로운 activity에 넘길 intent

                    val user = FirebaseAuth.getInstance().currentUser                //구글 계정으로 로그인된 사용자의 정보
                    user?.let {
                        val name = user.displayName
                        loginIntent.putExtra( "UserName",name)                              // 넘길 intent에 extra(user이름)넣음.
                        startActivityForResult(loginIntent, 1)
                    }

                } else {    //구글 인증 실패 시 , toast 메시지 띄어줌.
                   Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
           })
    }
    override fun onConnectionFailed(@NonNull connectionResult: ConnectionResult) {
                             //FirebaseAuth.getInstance().signOut() 로그아웃 구현하지 않았음.
    }
    // 구글로그인 result 상수
    //구글로그인 버튼을 클릭하여 startactivityforresult 응답 코드로 사용.
    companion object {
        private const val RC_SIGN_IN = 10
    }
}