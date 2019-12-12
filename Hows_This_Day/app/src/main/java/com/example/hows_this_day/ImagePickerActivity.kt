package com.example.hows_this_day

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import java.io.File

class ImagePickerActivity : AppCompatActivity() {

    // 필요한 변수 초기화
    private var lockAspectRatio = false
    private var setBitmapMaxWidthHeight = false
    private var ASPECT_RATIO_X = 16
    private var ASPECT_RATIO_Y = 9
    private var bitmapMaxWidth = 1000
    private var bitmapMaxHeight = 1000
    private var IMAGE_COMPRESSION = 80

    interface PickerOptionListener {
        fun onTakeCameraSelected()
        fun onChooseGallerySelected()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_picker)

        val intent = getIntent()

        if (intent == null) { // intent가 없을 때 메시지 보여주기
            Toast.makeText(
                getApplicationContext(),
                getString(R.string.toast_image_intent_null),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X)
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y)
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION)
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth)
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight)

        /**
         * 여러 개의 activity를 실행하고 각 activity에서 결과를 받을 때
         * 어떤 activity를 호출했는지 구별해주는 역할
        **/
        val requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1) // 카메라 or 갤러리 선택 옵션
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage()
        } else {
            chooseImageFromGallery()
        }
    }

    private fun takeCameraImage() { // 카메라로 찍기
        // 퍼미션 요청 런타임 과정을 심플하게 해주는 안드로이드 라이브러리
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) { // 모든 권한을 허용했을 때
                        fileName = System.currentTimeMillis().toString() + ".jpg" // 파일 네임 설정
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라 intent 만들기
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            // 미디어 데이터를 저장할 때는 MediaStore를 이용하기를 권장
                            getCacheImagePath(fileName) // 경로 가져오기
                        )
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // 결과가 없으면
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
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

    private fun chooseImageFromGallery() { // 갤러리에서 선택
        // 퍼미션 요청 런타임 과정을 심플하게 해주는 안드로이드 라이브러리
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent( // 이미지 intent 만들기
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            // 미디어 데이터를 저장할 때는 MediaStore를 이용하기를 권장
                        )
                        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE) // 결과 설정
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) { // 카메라로 찍었을 때 결과가 있다면
                cropImage(getCacheImagePath(fileName)) // 이미지 편집으로 넘어가기
            } else {
                setResultCancelled() // 아니면 종료
            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) { // 갤러리에서 가져올 때 결과가 있다면
                val imageUri = data?.data
                cropImage(imageUri) // 이미지 편집으로 넘어가기
            } else {
                setResultCancelled() // 아니면 종료
            }

            // UCrop : 이미지 크롭 라이브러리
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                handleUCropResult(data)
            } else {
                setResultCancelled()
            }
            UCrop.RESULT_ERROR -> {
                val cropError = data?.let { UCrop.getError(it) }
                Log.e(TAG, "Crop error: " + cropError!!)
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
    }

    private fun cropImage(sourceUri: Uri?) { // 이미지 편집
        val destinationUri =
            Uri.fromFile(File(getCacheDir(), queryName(getContentResolver(), sourceUri))) // 파일에서 uri 가져오기
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)

        if (lockAspectRatio)
            options.withAspectRatio(ASPECT_RATIO_X.toFloat(), ASPECT_RATIO_Y.toFloat()) // 가로 세로 비율 설정

        if (setBitmapMaxWidthHeight)
            options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight) // 최대 넓이 높이 설정

        UCrop.of(sourceUri!!, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) { // 데이터가 없으면 종료
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath: Uri?) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setResultCancelled() { // 종료
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String): Uri { // 경로 가져오기
        val path = File(getExternalCacheDir(), "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return getUriForFile(this@ImagePickerActivity, getPackageName() + ".provider", image)
    }

    // static 접근을 허용할 프로퍼티
    companion object {

        private val TAG = ImagePickerActivity::class.java.simpleName
        val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        val INTENT_BITMAP_MAX_WIDTH = "max_width"
        val INTENT_BITMAP_MAX_HEIGHT = "max_height"

        val REQUEST_IMAGE_CAPTURE = 0
        val REQUEST_GALLERY_IMAGE = 1
        lateinit var fileName: String

        fun showImagePickerOptions(context: Context, listener: PickerOptionListener) { // 선택 옵션 만들기

            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.getString(R.string.lbl_set_profile_photo))

            // 리스트 추가
            val animals = arrayOf(
                context.getString(R.string.lbl_take_camera_picture),
                context.getString(R.string.lbl_choose_from_gallery)
            )
            builder.setItems(animals) { _ , which ->
                when (which) {
                    0 -> listener.onTakeCameraSelected()
                    1 -> listener.onChooseGallerySelected()
                }
            }

            // 다이얼러그 만들고 보여주기
            val dialog= builder.create()
            dialog.show()
        }

        /**
         * 린트는 개발자가 완벽히 알맞은 코드나 충돌 가능성이 있는 코드를 사용할때 @SuppressLint(...)를 붙여 사용할 수 있게 해준다
         * @SuppressLint("NewApi")는 해당 프로젝트의 설정 된 minSdkVersion 이후에 나온 API를 사용할때
         * warning을 없애고 개발자가 해당 APi를 사용할 수 있게 한다
         */
        @SuppressLint("Recycle")
        private fun queryName(resolver: ContentResolver, uri: Uri?): String {
            val returnCursor = resolver.query(uri!!, null, null, null, null)!!
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            return name
        }

    }
}