package com.android.navigator

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.core.extensions.findMenuNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import kotlinx.android.synthetic.main.fragment_a.btnNext
import kotlinx.android.synthetic.main.fragment_a.txtName
import kotlinx.android.synthetic.main.fragment_d.*

class FragmentE : BaseFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_d, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtName.text = "Fragment E"
        btnNext.text = "Next to F"
        btnPopup.text = "Back to A - include"
        btnNext.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentF)
        }
        btnPopup.setOnClickListener {
            findMenuNavController().navigate(R.id.fragmentA, navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.fragmentA, true)
                .build())
        }
    }
}