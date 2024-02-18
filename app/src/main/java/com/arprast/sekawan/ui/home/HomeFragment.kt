package com.arprast.sekawan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arprastandroid.R

class HomeFragment : Fragment() {

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

            val root = inflater.inflate(R.layout.fragment_home, container, false)
            activity?.let {
                if(true){
                    openFragment(LoginFragment())
                }  else {
                    openFragment(HomeViewFragment())
                }
            }
            return root
    }
}