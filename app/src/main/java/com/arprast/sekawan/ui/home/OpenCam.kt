package com.arprast.sekawan.ui.home

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.ImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity

class OpenCam {

    lateinit var clickImageId: ImageView
    private lateinit var imageUri: Uri
    private lateinit var fragmentActivity: FragmentActivity
    private fun doOpnCam(fragmentActivity: FragmentActivity){
        val values = ContentValues()
        imageUri = fragmentActivity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        //submit listen open cam PICTURE_CAM_ID
//        startActivityForResult(cameraIntent, HomeFragment.PICTURE_CAM_ID)
    }
}