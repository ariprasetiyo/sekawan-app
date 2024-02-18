package com.arprast.sekawan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.arprastandroid.R

class SignUpFragment: Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var fragmentManager: FragmentManager

    private fun openFragment(fragment: Fragment) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.addToBackStack(null)
        transaction?.commit()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_signup, container, false)
        activity?.let {
            openSignUp(root, it)
        }
        return root
    }

    private fun openSignUp(root: View, it: FragmentActivity) {
        fragmentActivity = it
//
//        // sign in
//        val buttonLogin = root.findViewById<Button>(R.id.login_sign_in)
//        buttonLogin.setOnClickListener {
//            val inputUsername = root.findViewById<MaterialEditText>(R.id.login_input_username_or_email)
//            val inputPassword = root.findViewById<MaterialEditText>(R.id.login_input_password)
//            Log.d(PreferanceVariable.DEBUG_NAME, "username : ${inputUsername.text} dan password : ${inputPassword.text}")
//            if (inputUsername.text!!.toString() == ("ari12345678") && inputPassword.text!!.toString() == ("ari12345678")) {
//                Utils.showTost(context, "password sudah benar")
//                openFragment(HomeViewFragment())
//            }
//        }
//
//        // signup
//        val text = root.findViewById<MaterialTextView>(R.id.login_sign_up)
//        text.movementMethod = (object : TextViewLinkHandler(),
//            MovementMethod {
//            override fun onLinkClick(url: String?) {
//                Utils.showTost(context, "hallo")
//            }
//        })
        fragmentManager = (context as FragmentActivity).supportFragmentManager
    }
}