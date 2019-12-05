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
import com.example.hows_this_day.CalendarData
import com.example.hows_this_day.ImagePickerActivity
import com.example.hows_this_day.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.io.IOException
import java.util.*


class AFragment : Fragment() {

    internal var imgProfile: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //뷰 설정g
        tvFragmentMain
        onProflieClick()
        getDday()
    }


    private fun loadProfile(url: String) {
        Log.d(TAG, "Image cache path: $url")

        imgProfile = view?.findViewById(R.id.my_image)
        imgProfile?.let { Glide.with(this).load(url).into(it) }
        imgProfile?.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent))
    }

    private fun onProflieClick() {
        val my_btn: Button? = view?.findViewById(R.id.btn_my_UploadPicture)
        my_btn?.setOnClickListener {
            Dexter.withActivity(activity)
                .withPermissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            showSettingsDialog()
                        }
                    }

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
                override fun onTakeCameraSelected() {
                    launchCameraIntent()
                }

                override fun onChooseGallerySelected() {
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

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
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

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.getParcelableExtra<Uri>("path")
                try {
                    // You can update this bitmap to your server
                    MediaStore.Images.Media.getBitmap(activity?.contentResolver, uri)

                    // loading profile image from local cache
                    loadProfile(uri!!.toString())
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(getString(R.string.dialog_permission_title))
        builder.setMessage(getString(R.string.dialog_permission_message))
        builder.setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            getString(android.R.string.cancel)
        ) { dialog, _ -> dialog.cancel() }
        builder.show()

    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity?.packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    companion object {
        private val TAG = AFragment::class.java.simpleName
        val REQUEST_IMAGE = 100
    }

    // Fragment에서 getActivity()와 getContext()가 null을 반환하여 만드는 코드 부분
    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    // 사귀기 시작한 날짜로부터 D+Day
    private fun getDday() {
        // 파이어베이스에 있는 날짜 가져오기
        var sDay: Int? = 0
        var sMonth: Int? = 0
        var sYear: Int? = 0
        val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("User")
        val roomDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Room")
        val user = FirebaseAuth.getInstance().currentUser
        val postReference = mDatabase.child(user!!.uid)
        val BigListener = object : ValueEventListener {
            //User에서 데이터 불러옴
            override fun onCancelled(p0: DatabaseError) {
                //데이터 불러오기 실패
            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                //데이터 변화를 감지했을 때
                val postListener = object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(datasnapshot: DataSnapshot) {
                        val Start =
                            datasnapshot.child("StartDay").getValue(CalendarData::class.java)
                        Start?.let {
                            Log.d("데이데이",sDay.toString())
                            sDay = it.Day
                            sMonth = it.Month
                            sYear = it.Year
                        }

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
                        val oneDay: Long = 24 * 60 * 60 * 1000
                        val dday = ddayCalendar.getTimeInMillis() / oneDay
                        val today = Calendar.getInstance().getTimeInMillis() / oneDay
                        val dday_from_today: Long = (today - dday) + 1

                        start_day?.setText(String.format("❝ %d년 %d월 %d일 ❞", sYear, sMonth, sDay))
                        d_day?.setText(String.format("D+%d", dday_from_today))
                    }
                }
                val coupleRoom = datasnapshot.child("CoupleRoom").getValue(String::class.java)
                //User에서 커플룸 이름 읽어옴
                val roomReference = coupleRoom?.let { roomDatabase.child(it) }
                roomReference?.addValueEventListener(postListener)
                // User에서 커플룸 이름 불러오고, Room에 커플룸 이름으로 방을 생성 및 달력 데이터 불러옴
            }
        }
        postReference.addValueEventListener(BigListener)
        Log.d("프로필",sDay.toString())

    }
}