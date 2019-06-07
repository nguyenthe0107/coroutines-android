package com.android.navigator

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.extensions.findMenuNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import kotlinx.android.synthetic.main.fragment_d.*

class FragmentD : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_d, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtName.text = "Fragment D "
        btnNext.text = "Next to E"
        btnPopup.text = "Back to A"
        btnNext.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentE)
        }
        btnPopup.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentA, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentA, false)
                .build())
        }
    }
}