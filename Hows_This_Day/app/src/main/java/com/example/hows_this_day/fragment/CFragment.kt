package com.example.hows_this_day.fragment
//다이어리 Activity
//사진 저장 , 불러오기

import android.Manifest
import com.example.hows_this_day.ImagePickerActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

import android.app.Activity
import android.app.AlertDialog
import android.content.Context

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import java.util.ArrayList
import java.util.HashMap
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.hows_this_day.R
import java.io.IOException
import android.widget.Button
import com.google.firebase.storage.FirebaseStorage


class CFragment : Fragment() {

    internal var picture: ImageView? = null
    lateinit var firebaseStorage: FirebaseStorage

    var dialogItemList: MutableList<Map<String, Any>>? = null                             //날짜선택 list
    var image = intArrayOf(R.drawable.redheart, R.drawable.redheart, R.drawable.redheart)   //날짜 선택 dialog에 나타날 이미지
    var text = arrayOf("2019-11-21", "2019-11-22", "2019-11-23")                        //sample date


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialogItemList = ArrayList()

        //날짜 불러오기
        for (i in image.indices) {
            val itemMap = HashMap<String, Any>()
            itemMap[TAG_IMAGE] = image[i]
            itemMap[TAG_TEXT] = text[i]
            dialogItemList?.add(itemMap)
        }


        return inflater.inflate(R.layout.fragment_c, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseStorage = FirebaseStorage.getInstance()

        val button_run = getView()?.findViewById<View>(R.id.bt_datechooser) as Button?
        button_run?.setOnClickListener { showAlertDialog() }        //날짜 선택 다이얼로그를 호출

        onPictureClick()
    }

    private fun loadPicture(url: String) {

        picture = getView()?.findViewById(R.id.bt_gallery)
        picture?.let { Glide.with(this).load(url).into(it) }
        picture?.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent))
    }

    private fun onPictureClick() {
        val my_btn : ImageButton? = getView()?.findViewById(R.id.get_picture)
        my_btn?.setOnClickListener {
            Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                        permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
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
        val intent = Intent(getActivity(), ImagePickerActivity::class.java)
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
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 800)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 800)

        startActivityForResult(intent, REQUEST_IMAGE)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(getActivity(), ImagePickerActivity::class.java)
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
                    MediaStore.Images.Media.getBitmap(getActivity()?.getContentResolver(), uri)

                    // loading profile image from local cache
                    loadPicture(uri.toString())
                    // 파이어베이스 스토리지에 저장하기
                    uri?.let { firebaseStorage.reference.child("diaryFolder").child("diaryPicture.png").putFile(it) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

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
        val uri = Uri.fromParts("package", getActivity()?.getPackageName(), null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    // Fragment에서 getActivity()와 getContext()가 null을 반환하여 만드는 코드 부분
    lateinit var mContext : Context
    override fun onAttach(context : Context) {
        super.onAttach(context)
        mContext = context
    }

    //날짜 다이얼로그
    private fun showAlertDialog() {

        val tvresult = getView()?.findViewById<View>(R.id.textview_main_text) as TextView?
        val builder = AlertDialog.Builder(getActivity())
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.alert_dialog, null)
        builder.setView(view)

        val listview = view.findViewById<View>(R.id.listview_alterdialog_list) as ListView
        val dialog = builder.create()

        val simpleAdapter = SimpleAdapter(
            getActivity(), dialogItemList,
            R.layout.alert_dialog_row,
            arrayOf(TAG_IMAGE, TAG_TEXT),
            intArrayOf(R.id.alertDialogItemImageView, R.id.alertDialogItemTextView)
        )

        listview.adapter = simpleAdapter
        listview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->

                tvresult?.text = (text[position] )
                dialog.dismiss()
            }

        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialog.show()
    }

    companion object {
        private val TAG = AFragment::class.java.simpleName
        val REQUEST_IMAGE = 100
        private val TAG_TEXT = "text"
        private val TAG_IMAGE = "image"
    }

}