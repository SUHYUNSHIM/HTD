package com.example.hows_this_day.fragment


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.hows_this_day.CalendarData
import com.example.hows_this_day.ImagePickerActivity
import com.example.hows_this_day.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_a.*
import java.io.IOException
import java.util.*


class AFragment : Fragment() {

    // 각각 늦은 초기화
    lateinit var firebaseStorage: FirebaseStorage
    internal var imgProfile: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        firebaseStorage = FirebaseStorage.getInstance()

        onProflieClick()
        getDday()
    }


    private fun loadProfile(url: String) {
        // 이미지 프로필 설정
        imgProfile = view?.findViewById(R.id.my_image)
        imgProfile?.let { Glide.with(this).load(url).into(it) }
        imgProfile?.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent))
    }

    private fun onProflieClick() {
        val my_btn: Button? = view?.findViewById(R.id.btn_my_UploadPicture)
        my_btn?.setOnClickListener {    // SELECT 버튼이 클릭되었을 때
            // 퍼미션 요청 런타임 과정을 심플하게 해주는 안드로이드 라이브러리
            Dexter.withActivity(activity)
                .withPermissions( // 퍼미션 체크
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener { // 권한을 전부 허용했을 경우
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) { // 권한을 전부 허용했을 경우
                            showImagePickerOptions() // 카메라 or 갤러리에서 선택
                        }

                        if (report.isAnyPermissionPermanentlyDenied) { // 권한을 거부했을 경우
                            showSettingsDialog() // 경고 안내메세지 보여주기
                        }
                    }

                    // 사용자가 위험하다고 판단되거나 사용자가 이미 한 번 해당 권한을 거부했기 때문에
                    // 앱 사용에 대한 추가적인 설명이 필요한 경우 권한을 요청할 때 안드로이드가 사용자에게 통지하게 한다.
                    // 토큰이 사용될 때까지 요청 프로세스가 일시정지 되므로 토큰이 사용되지 않은 경우
                    // Dexter를 다시 call하거나 다른 권한을 요청할 수없으니 주의.
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }
    }

    private fun showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(mContext,
            object : ImagePickerActivity.PickerOptionListener {
                override fun onTakeCameraSelected() { // 카메라를 선택했을 때
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() { // 갤러리를 선택했을 때
                    launchGalleryIntent()
                }
            })
    }

    private fun launchCameraIntent() {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        // 가로 세로 비율 설정
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // 최대 bitmap 너비 및 높이 설정
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

        // 가로 세로 비율 설정
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // 갤러리는 이미 찍어놓은 사진들이기 때문에 최대 너비 및 높이 설정 필요 없음

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) { // 선택 후 결과값
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.getParcelableExtra<Uri>("path")
                try {
                    // 미디어 데이터를 저장할 때는 MediaStore를 이용하기를 권장
                    // 이 bitmap을 서버로 업데이트
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)

                    loadProfile(uri.toString()) // 프로필 설정

                    // 파이어베이스 스토리지에 업로드 하기
                    // profileFolder 디텍토리에 myProfile 이름으로 저장
                    uri?.let { firebaseStorage.reference.child("profileFolder").child("myProfile.png").putFile(it) }
                } catch (e: IOException) {
                    e.printStackTrace() // 실패할 경우 오류 출력
                    /**
                     * printStackTrace는 리턴값이 없으며
                     * 이 메소드를 호출하면 메소드가 내부적으로 예외 결과를 화면에 출력한다.
                     * printStackTrace는 가장 자세한 예외 정보를 제공한다.
                     **/
                }

            }
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(getString(R.string.dialog_permission_title)) // 타이틀 설정
        builder.setMessage(getString(R.string.dialog_permission_message)) // 권한 메시지 다이얼로그 보여주기
        builder.setPositiveButton(getString(R.string.go_to_settings)) // 허용 버튼을 누를 때
        { dialog, _ -> dialog.cancel()
            openSettings() }
        builder.setNegativeButton( // 거부 버튼을 누를 때
            getString(android.R.string.cancel)
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    // 앱 설정으로 안내
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101) // requestCode를 101로 설정
    }

    // static 접근을 허용할 프로퍼티
    companion object {
        val REQUEST_IMAGE = 100
    }

    // 파이어베이스 스토리지에서  이미지 가져오기
    private fun downloadInLocal() {
        // 해당 주소로 가서 reference 가져오기
        val ref : StorageReference = FirebaseStorage.getInstance()
            .getReference("gs://how-this-day.appspot.com/profileFolder/myProfile.png")

        // 가져온 이미지를 설정
        imgProfile = view?.findViewById(R.id.my_image)
        //imgProfile?.let { Glide.with(this).load(ref).into(it) }
        //imgProfile?.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent))
        imgProfile?.let {
            Glide.with(this)
                .load(ref)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(it)
        }
    }

    // Fragment에서 getActivity()와 getContext()가 null을 반환하여 만드는 코드 부분
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    // 사귀기 시작한 날짜로부터 D+Day
    private fun getDday() {
        // 0으로 초기화
        var sDay: Int? = 0
        var sMonth: Int? = 0
        var sYear: Int? = 0

        // 파이어베이스에서 가져오기 위해 필요한 reference 선언
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
        val roomDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
        val user = FirebaseAuth.getInstance().currentUser
        val postReference = mDatabase.child(user!!.uid)

        val BigListener = object : ValueEventListener {
            // User에서 데이터 불러옴
            override fun onCancelled(p0: DatabaseError) {
                //데이터 불러오기 실패
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                //데이터 변화를 감지했을 때
                val postListener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    // 파이어베이스에서 사귀기 시작한 날짜 가져오기
                    override fun onDataChange(datasnapshot: DataSnapshot) {
                        val Start =
                            datasnapshot.child("StartDay").getValue(CalendarData::class.java)
                        Start?.let {
                            Log.d("데이데이", sDay.toString())
                            sDay = it.Day
                            sMonth = it.Month
                            sYear = it.Year
                        }

                        // 사귀기 시작한 날짜로 세팅
                        val ddayCalendar = Calendar.getInstance()
                        sYear?.let {
                            sMonth?.let { it1 ->
                                sDay?.let { it2 ->
                                    ddayCalendar.set(
                                        it,
                                        it1 - 1,
                                        it2
                                    )
                                }
                            }
                        }

                        // Millisecond 형태의 하루(24시간)
                        val oneDay: Long = 24 * 60 * 60 * 1000 // 초단위
                        val dday = ddayCalendar.getTimeInMillis() / oneDay // 사귀기 시작한 날짜를 나누기(일 단위)
                        val today = Calendar.getInstance().getTimeInMillis() / oneDay // 현재 날짜를 나누기(일 단위)
                        val dday_from_today: Long = (today - dday) + 1 // 현재 날짜 - 사귀기 시작한 날짜
                        // 사귀기 시작한 날부터 1일로 치기 때문에 +1

                        // 텍스트 설정
                        start_day?.setText(String.format("❝ %d년 %d월 %d일 ❞", sYear, sMonth, sDay))
                        d_day?.setText(String.format("D+%d", dday_from_today))
                    }
                }

                val coupleRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
                // User에서 커플룸 이름을 읽어온다
                val roomReference = coupleRoom?.let { roomDatabase.child(it) }
                roomReference?.addValueEventListener(postListener)
                // User에서 커플룸 이름 불러온 후 Room에 있는 첫날 데이터 불러온다
            }
        }
        postReference.addValueEventListener(BigListener)
        Log.d("프로필", sDay.toString())
    }
}