package com.kantek.coroutines.viewmodel

import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.map
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppViewModel
import com.kantek.coroutines.app.alert
import com.kantek.coroutines.app.fail
import com.kantek.coroutines.app.snack
import com.kantek.coroutines.repository.LoginRepository

class LoginViewModel(private val loginRepository: LoginRepository) : AppViewModel() {
    val form = SingleLiveEvent<Pair<String?, String?>>()

    val user = form.map(this) {
        if (it == null) fail(R.string.error_login_form)
        if (it.first.isNullOrEmpty()) alert(R.string.error_user_name)
        if (it.second.isNullOrEmpty()) snack(R.string.error_password)
        loginRepository.login(it)
    }
}