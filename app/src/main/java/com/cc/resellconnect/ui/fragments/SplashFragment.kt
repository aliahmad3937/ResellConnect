package com.cc.resellconnect.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.cc.resellconnect.R
import java.util.*


class SplashFragment : Fragment() {


    private lateinit var v: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_splash, container, false)
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                findNavController().navigate(R.id.userLogin)

        }, 3000)


        return v
    }


}