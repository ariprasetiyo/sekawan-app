package com.arprast.sekawan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.arprastandroid.R

class LoginFragment : Fragment() {

    private lateinit var fragmentActivity: FragmentActivity
    private lateinit var fragmentManager: FragmentManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_login, container, false)

        activity?.let {
            fragmentActivity = it
            fragmentManager = (context as FragmentActivity).supportFragmentManager
        }

        return root
    }
}