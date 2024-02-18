package com.arprast.sekawan.ui.home

import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.arprast.sekawan.util.PreferanceVariable
import com.arprastandroid.R
import com.google.android.material.textview.MaterialTextView
import com.rengwuxian.materialedittext.MaterialEditText

class LoginFragment : Fragment() {

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

        val root = inflater.inflate(R.layout.fragment_login, container, false)
        activity?.let {
            openLogin(root, it)
        }
        return root
    }

    private fun openLogin(root: View, it: FragmentActivity) {
        fragmentActivity = it

        // sign in
        val buttonLogin = root.findViewById<Button>(R.id.login_sign_in)
        buttonLogin.setOnClickListener {
            val inputUsername = root.findViewById<MaterialEditText>(R.id.login_input_username_or_email)
            val inputPassword = root.findViewById<MaterialEditText>(R.id.login_input_password)
            Log.d(PreferanceVariable.DEBUG_NAME, "username : ${inputUsername.text} dan password : ${inputPassword.text}")
            if (inputUsername.text!!.toString() == ("ari12345678") && inputPassword.text!!.toString() == ("ari12345678")) {
                openFragment(HomeViewFragment())
            }
        }

        // signup
        val text = root.findViewById<MaterialTextView>(R.id.login_sign_up)
        text.movementMethod = (object : TextViewLinkHandler(),
            MovementMethod {
            override fun onLinkClick(url: String?) {
               openFragment(SignUpFragment())
            }
        })
        fragmentManager = (context as FragmentActivity).supportFragmentManager
    }

    private abstract class TextViewLinkHandler : LinkMovementMethod() {
        override fun onTouchEvent(
            widget: TextView,
            buffer: Spannable,
            event: MotionEvent
        ): Boolean {
            if (event.action != MotionEvent.ACTION_UP) return super.onTouchEvent(
                widget,
                buffer,
                event
            )
            var x = event.x.toInt()
            var y = event.y.toInt()
            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop
            x += widget.scrollX
            y += widget.scrollY
            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x.toFloat())
            val link = buffer.getSpans(off, off, URLSpan::class.java)
            if (link.isNotEmpty()) {
                onLinkClick(link[0].url)
            }
            return true
        }

        abstract fun onLinkClick(url: String?)
    }
}