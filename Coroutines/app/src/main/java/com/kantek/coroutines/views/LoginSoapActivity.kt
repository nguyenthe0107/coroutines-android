package com.kantek.coroutines.views

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.observe
import android.widget.Toast
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.viewmodel.LoginSoapViewModel
import kotlinx.android.synthetic.main.activity_login.*

@LayoutId(R.layout.activity_login)
class LoginSoapActivity : AppActivity<LoginSoapViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnLogin.setOnClickListener {
            viewModel.form.value = edtUserName.text.toString() to edtPassword.text.toString()
        }
        viewModel.user.observe(this) {
            Toast.makeText(this, it!!.body?.response?.loginReturn ?: "", Toast.LENGTH_SHORT).show()
        }
    }
}
