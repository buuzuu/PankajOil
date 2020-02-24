package com.example.jetpack_kotlin.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.pankajoil.MainActivity
import com.example.pankajoil.R

class ProfileFragment : Fragment() {


    lateinit var logout:Button


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        logout = root.findViewById(R.id.logout)
        logout.setOnClickListener {

        }
        return root
    }
}