package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.observe
import android.view.View
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import com.kantek.coroutines.models.User
import com.kantek.coroutines.viewmodel.MainViewModel
import com.kantek.coroutines.views.dialogs.EditDialog
import kotlinx.android.synthetic.main.fragment_profile.*

@LayoutId(R.layout.fragment_profile)
class ProfileFragment : AppFragment<MainViewModel>() {

    private lateinit var mDialog: EditDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDialog = EditDialog(this)
        viewModel.profile.observe(this) {
            txtName.text = it!!.name
            txtEmail.text = it.email
            txtPhone.text = it.phone
            txtAddress.text = it.address.toString()
            txtWebsite.text = it.website
            txtCompanyName.text = it.company.name
            txtCatchPhrase.text = it.company.catchPhrase
            txtBs.text = it.company.bs
        }
        txtName.setOnClickListener { mDialog.show(it.id, txtName.text.toString()) }
        txtEmail.setOnClickListener { mDialog.show(it.id, txtEmail.text.toString()) }
        txtPhone.setOnClickListener { mDialog.show(it.id, txtPhone.text.toString()) }
        mDialog.onOkClickListener = { viewModel.updateProfile(it) }
    }

}