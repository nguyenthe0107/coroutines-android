package com.kantek.coroutines.views

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.extensions.observe
import android.support.core.functional.Dispatcher
import android.support.core.functional.close
import android.support.core.functional.open
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppActivity
import com.kantek.coroutines.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

@LayoutId(R.layout.activity_login)
class LoginActivity : AppActivity<LoginViewModel>() {
    companion object {
        fun show(from: Dispatcher) {
            from.open<LoginActivity>().close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btnLogin.setOnClickListener {
            viewModel.form.value = edtUserName.text.toString() to edtPassword.text.toString()
        }
        viewModel.user.observe(this) {
            open(MainActivity::class).close()
        }
    }
}
