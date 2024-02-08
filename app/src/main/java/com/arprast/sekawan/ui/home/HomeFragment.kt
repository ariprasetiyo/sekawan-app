package com.arprast.sekawan.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arprast.sekawan.type.RegistrationActivityType
import com.arprast.sekawan.util.Utils
import com.arprast.sekawan.util.Utils.DatePickerFragment
import com.arprastandroid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var clickImageId: ImageView
    private lateinit var imageUri: Uri
    private var registerId: RegistrationActivityType = RegistrationActivityType.NOTHING
    private lateinit var fragmentManager: FragmentManager

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
            mainMenu(bottomNavigationView, it)
            fragmentManager = (context as FragmentActivity).supportFragmentManager
        }

        /** disabled email hover menu */
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

    private fun mainMenu(bottomNavigationView: BottomNavigationView, it: FragmentActivity) {

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.home_menu_trx -> {
                        Utils.showTost(requireContext(), "trx list not yet support")
                    }

                    R.id.home_menu_open_cam -> {
                        doOpenCam()
                    }

                    R.id.home_menu_notification -> {
                        Utils.showTost(requireContext(), "notification not yet support")
                    }
                }
                return true
            }
        })
    }

    private fun doOpenCam() {

        registerId = RegistrationActivityType.OPEN_CAM_FOR_PHOTO
        val values = ContentValues()
        imageUri = fragmentActivity.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )!!

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

//        activity?.setResult(Activity.RESULT_OK)
//        activity?.finish()
        startActivityIntent.launch(cameraIntent)
    }

    private var startActivityIntent = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && registerId == RegistrationActivityType.OPEN_CAM_FOR_PHOTO) {
            val thumbnail =
                MediaStore.Images.Media.getBitmap(fragmentActivity.contentResolver, imageUri)
            clickImageId.setImageBitmap(thumbnail)

            val options = RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
            Glide.with(fragmentActivity).load(Utils.getPathImage(imageUri, context)).apply(options)
                .into(clickImageId)

            showDialogTextInputLayout(this.requireContext())
        }
    }

    private fun showDialogTextInputLayout(context: Context) {

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(
            resources.getDimensionPixelOffset(R.dimen.dp_19),
            0,
            resources.getDimensionPixelOffset(R.dimen.dp_19),
            0
        )

        val customerName = EditText(context)
        customerName.hint = getString(R.string.input_customer_name)
        layout.addView(customerName)

        val customerPhoneNumber = EditText(context)
        customerPhoneNumber.hint = getString(R.string.input_customer_phone_number)
        customerPhoneNumber.inputType = InputType.TYPE_CLASS_NUMBER
        customerPhoneNumber.filters = (arrayOf<InputFilter>(LengthFilter(14)))
        layout.addView(customerPhoneNumber)

        customerPhoneNumber.onFocusChangeListener = (object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    val isValidPhoneNo = Utils.isValidPhoneNumber(customerPhoneNumber.text.toString())
                    if(!isValidPhoneNo){
                        customerPhoneNumber.hint = getString(R.string.input_customer_phone_number_warning)
                        customerPhoneNumber.setHintTextColor(getResources().getColor(R.color.redColor))
                        customerPhoneNumber.text.clear()
                    }
                }
            }
        })

        val address = EditText(context)
        address.hint = getString(R.string.input_customer_address)
        layout.addView(address)

        val estimationDate = EditText(context)
        estimationDate.hint = getString(R.string.input_customer_estimation_date)
        layout.addView(estimationDate)

        estimationDate.onFocusChangeListener = (object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    val dialogFragment = DatePickerFragment(estimationDate)
                    dialogFragment.show(fragmentManager, "datePicker")
                }
            }
        })

        val alert = AlertDialog.Builder(context)
            .setTitle("Input data customer")
            .setView(layout)
            .setMessage("Please input the data")
            .setPositiveButton("Submit") { dialog, _ ->
                dialog.cancel()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }
}