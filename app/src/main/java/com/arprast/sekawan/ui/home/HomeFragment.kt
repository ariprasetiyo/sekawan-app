package com.arprast.sekawan.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.arprast.sekawan.type.GenderType
import com.arprast.sekawan.type.MeasureType
import com.arprast.sekawan.type.RegistrationActivityType
import com.arprast.sekawan.util.Utils
import com.arprastandroid.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.rengwuxian.materialedittext.MaterialEditText
import java.io.File

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

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        activity?.let {
            openHome(root, it)
        }
        return root
    }

    private fun openHome(root: View, it: FragmentActivity) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        clickImageId = root.findViewById(R.id.view_open_cam)

        fragmentActivity = it
        val bottomNavigationView =
            root.findViewById(R.id.home_bottom_navigation) as BottomNavigationView
        mainMenu(bottomNavigationView)
        fragmentManager = (context as FragmentActivity).supportFragmentManager

        /** disabled email hover menu */
        val fab: FloatingActionButton = root.findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            val snackbar = Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)

            val view: View = snackbar.view
            val params = view.layoutParams as CoordinatorLayout.LayoutParams
            params.gravity = Gravity.TOP
            view.layoutParams = params
            snackbar.setAction("Action", null).show()
        }

        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
    }

    private fun mainMenu(bottomNavigationView: BottomNavigationView) {

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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
            true
        }
    }

    private fun doOpenCam() {

        registerId = RegistrationActivityType.OPEN_CAM_FOR_PHOTO
        val values = ContentValues()
        values.put(
            MediaStore.Images.Media.DISPLAY_NAME,
            "capture_image_${System.currentTimeMillis()}"
        );
        values.put(MediaStore.Images.Media.DESCRIPTION, "this is description");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

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
            showImageCam()
        }
    }

    private fun showImageCam() {
        val thumbnail =
            MediaStore.Images.Media.getBitmap(fragmentActivity.contentResolver, imageUri)
        clickImageId.setImageBitmap(thumbnail)

        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
        Glide.with(fragmentActivity).load(Utils.getPathImage(context, imageUri)).apply(options)
            .into(clickImageId)

        showDialogTextInputLayout(this.requireContext())
    }

    private fun showDialogTextInputLayout(context: Context) {

        val dateNow = Utils.getDateNow()
        val builder = AlertDialog.Builder(context)
        // Get the layout inflater.
        val inflater = requireActivity().layoutInflater

        val viewAlertDialog = inflater.inflate(R.layout.dialog_after_open_cam, null)
        val customerTrxDate = viewAlertDialog.findViewById<MaterialTextView>(R.id.input_customer_view_trx_date)
        customerTrxDate.text = "$dateNow"

        val customerTrxNo = viewAlertDialog.findViewById<MaterialTextView>(R.id.input_customer_view_trx_no)
        customerTrxNo.text = "${getString(R.string.input_customer_trx_no)} ${Utils.getTrxNo()}"

        val customerName = viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_name)
        customerName.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        customerName.isSingleLine = true

        val customerPhoneNo =
            viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_phone_number)
        customerPhoneNo.inputType = (InputType.TYPE_CLASS_NUMBER)
        customerPhoneNo.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(13)))
        customerPhoneNo.isSingleLine = true
        customerPhoneNo.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val isValidPhoneNo = Utils.isValidPhoneNumber(customerPhoneNo.text.toString())
                if (!isValidPhoneNo) {
                    customerPhoneNo.hint = getString(R.string.input_customer_phone_number_warning)
                    customerPhoneNo.setHintTextColor(resources.getColor(R.color.redColor))
                    customerPhoneNo.text!!.clear()
                }
            }
        })

        val customerMeasure =
            viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_measure_input)
        customerMeasure.inputType = (InputType.TYPE_CLASS_NUMBER)
        customerMeasure.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(5)))
        customerMeasure.isSingleLine = true

        val adapter = ArrayAdapter(
            context,
            R.layout.account_type_dropdown_menu,
            MeasureType.values()
        )
        val editTextFilledExposedDropdown = viewAlertDialog.findViewById<AutoCompleteTextView>(R.id.input_customer_measure)
        editTextFilledExposedDropdown.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(1)))
        editTextFilledExposedDropdown.isSingleLine = true
        editTextFilledExposedDropdown.setText(adapter.getItem(0)!!.stringValue, true)
        editTextFilledExposedDropdown.setAdapter(adapter)

        val customerEstimationDate =
            viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_estimation_date)
        customerEstimationDate.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(10)))
        customerEstimationDate.onFocusChangeListener = (View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val dialogFragment = Utils.DatePickerFragment(customerEstimationDate)
                dialogFragment.show(fragmentManager, "datePicker")
            }
        })
        val customerAddress =
            viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_address);
        customerAddress.isSingleLine = true
        customerAddress.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(100)))


        val customerNote = viewAlertDialog.findViewById<MaterialEditText>(R.id.input_customer_note);
        customerAddress.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(200)))

        val builderAlertDialog = builder.setView(viewAlertDialog)
            // Add action buttons.
            .setPositiveButton(getString(R.string.button_save),
                DialogInterface.OnClickListener { dialog, id ->
                    tostText("save success ${customerName.text}")
                })
            .setNeutralButton(getString(R.string.button_cancel),
                DialogInterface.OnClickListener { dialog, id ->
                    deleteFileImageCam()
                    dialog.cancel()
                })
        builderAlertDialog.create()
        builderAlertDialog.show()


//        val layout = LinearLayout(context)
//        layout.orientation = LinearLayout.VERTICAL
//        layout.setPadding(
//            resources.getDimensionPixelOffset(R.dimen.dp_19),
//            0,
//            resources.getDimensionPixelOffset(R.dimen.dp_19),
//            0
//        )
//
//        layout.addView(inputCustomerName())
//        layout.addView(inputCustomerPhoneNumber())
//        layout.addView(inputCustomerEstimationDate())
//        layout.addView(inputCustomerAddress())
//        layout.addView(inputCustomerNote())
//
//        val dateNow = Utils.getDateNow()
//        val alert = AlertDialog.Builder(context)
//            .setTitle("${getString(R.string.input_customer_title)} $dateNow")
//            .setMessage("Trx No ${Utils.getTrxNo()}")
//            .setView(layout)
//            .setPositiveButton(getString(R.string.button_save)) { dialog, _ ->
//                dialog.cancel()
//            }
//            .setNeutralButton(getString(R.string.button_cancel)) { dialog, _ ->
//                deleteFileImageCam()
//                dialog.cancel()
//            }.create()
//
//        alert.show()
//

    }

    private fun inputCustomerName(): MaterialEditText {
        val customerName = MaterialEditText(context)
        customerName.hint = getString(R.string.input_customer_name)
        customerName.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(50)))
        customerName.isSingleLine = true
        customerName.onFocusChangeListener = (object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    if (customerName.text!!.isBlank()) {
                        customerName.hint = getString(R.string.input_customer_name_warning)
                        customerName.setHintTextColor(getResources().getColor(R.color.redColor))
                        customerName.text!!.clear()
                    }
                }
            }
        })
        return customerName
    }

    private fun inputCustomerPhoneNumber(): MaterialEditText {
        val customerPhoneNumber = MaterialEditText(context)
        customerPhoneNumber.hint = getString(R.string.input_customer_phone_number)
        customerPhoneNumber.inputType = (InputType.TYPE_CLASS_NUMBER)
        customerPhoneNumber.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(14)))
        customerPhoneNumber.onFocusChangeListener = (object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    val isValidPhoneNo =
                        Utils.isValidPhoneNumber(customerPhoneNumber.text.toString())
                    if (!isValidPhoneNo) {
                        customerPhoneNumber.hint =
                            getString(R.string.input_customer_phone_number_warning)
                        customerPhoneNumber.setHintTextColor(getResources().getColor(R.color.redColor))
                        customerPhoneNumber.text!!.clear()
                    }
                }
            }
        })
        return customerPhoneNumber
    }

    private fun inputCustomerEstimationDate(): MaterialEditText {
        val estimationDate = MaterialEditText(context)
        estimationDate.hint = getString(R.string.input_customer_estimation_date)
        estimationDate.onFocusChangeListener = (object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    val dialogFragment = Utils.DatePickerFragment(estimationDate)
                    dialogFragment.show(fragmentManager, "datePicker")
                }
            }
        })
        return estimationDate
    }

    private fun inputCustomerAddress(): MaterialEditText {
        val address = MaterialEditText(context)
        address.hint = getString(R.string.input_customer_address)
        address.isSingleLine = false
        address.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        address.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        address.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(150)))
        return address
    }

    private fun inputCustomerNote(): MaterialEditText {
        val note = MaterialEditText(context)
        note.hint = getString(R.string.input_customer_note)
        note.isSingleLine = false
        note.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
        note.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        note.filters = (arrayOf<InputFilter>(InputFilter.LengthFilter(150)))
        return note;
    }

    private fun deleteFileImageCam() {
        val finalFilePath = Utils.getPathImage(this.context, imageUri)
        Utils.showTost(requireContext(), "found $finalFilePath")
        val fdelete = File(finalFilePath)
        if (fdelete.delete()) {
            Utils.showTost(context, "file deleted :")
        } else {
            Utils.showTost(context, "file not deleted : ")
        }
    }

    private fun tostText(text: String) {
        activity.let {
            val invalidateInputMesssage =
                Toast.makeText(it, text, Toast.LENGTH_LONG)
            invalidateInputMesssage.setGravity(Gravity.CENTER, 0, 0)
            invalidateInputMesssage.show()
        }
    }
}