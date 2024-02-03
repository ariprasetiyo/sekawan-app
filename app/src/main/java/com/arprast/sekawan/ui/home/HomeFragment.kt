package com.arprast.sekawan.ui.home

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arprast.sekawan.util.Utils
import com.arprastandroid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    lateinit var clickImageId: ImageView
    private lateinit var imageUri: Uri
    private lateinit var fragmentActivity: FragmentActivity

    companion object {
        private const val PICTURE_CAM_ID = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        clickImageId = root.findViewById(R.id.view_open_cam)

        activity?.let {
            fragmentActivity = it
            val bottomNavigationView =
                root.findViewById(R.id.home_bottom_navigation) as BottomNavigationView
            openCam(bottomNavigationView, it)
        }

        /**
         * disabled email hover menu
         */
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            val snackbar = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)

            val view: View = snackbar.getView()
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            snackbar.setAction("Action", null).show()
        }

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }

    private fun openCam(bottomNavigationView: BottomNavigationView, it: FragmentActivity) {

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.home_menu_trx -> {
                        tostText("trx list not yet support")
                    }

                    R.id.home_menu_open_cam -> {

                        val values = ContentValues()
                        imageUri = fragmentActivity.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )!!
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                        startActivityForResult(cameraIntent, PICTURE_CAM_ID)
                    }

                    R.id.home_menu_notification -> {
                        tostText("notification not yet support")
                    }
                }
                return true
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Match the request 'pic id with requestCode
        if (requestCode == PICTURE_CAM_ID) {

            // BitMap is data structure of image file which store the image in memory
            //val photo = data!!.extras!!["data"] as Bitmap?
            // Set the image in imageview for display
            //  clickImageId.setImageBitmap(photo)

            // traditional method load image
            // val thumbnail = MediaStore.Images.Media.getBitmap(fragmentActivity.contentResolver, imageUri)
            // clickImageId.setImageBitmap(thumbnail)

            val thumbnail =
                MediaStore.Images.Media.getBitmap(fragmentActivity.contentResolver, imageUri)
            clickImageId.setImageBitmap(thumbnail)

            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
            Glide.with(fragmentActivity).load(Utils.getPathImage(imageUri, context)).apply(options).into(clickImageId);

        }
    }

    private fun tostText(text: String) {

        val invalidateInputMesssage =
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG)
        invalidateInputMesssage.setGravity(Gravity.CENTER, 0, 0)
        invalidateInputMesssage.show()

    }
}