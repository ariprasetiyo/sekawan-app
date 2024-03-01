package com.arprast.sekawan.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import com.arprast.sekawan.repository.RealmDBRepository
import com.arprastandroid.R
import kotlin.system.exitProcess

class LogoutFragment : Fragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_logout, container, false)
        activity?.let {

            val auth = RealmDBRepository().getAuth()
            if(auth != null ){
                RealmDBRepository().deleteAuth(auth)
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().clear().apply();
            requireContext().getSharedPreferences("secure_prefs", Context.MODE_PRIVATE).edit().clear().apply()
            exitProcess(-1)
            finishAffinity(requireActivity())
        }
        return root
    }
}

