package com.android.navigator

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.extensions.findMenuNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import kotlinx.android.synthetic.main.fragment_f.*

class FragmentF : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_f, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn1.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentC, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentB, false)
                .build())
        }
        btn2.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentC, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentB, true)
                .build())
        }
        btn3.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentC, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentB, false)
                .setLaunchSingleTop(true)
                .build())
        }
        btn4.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentC, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentB, true)
                .setLaunchSingleTop(true)
                .build())
        }
    }
}