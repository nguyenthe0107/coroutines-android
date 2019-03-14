package com.kantek.coroutines.viewmodel

import android.support.core.base.BaseViewModel
import android.support.core.event.SingleLiveEvent
import android.support.core.extensions.mapLaunch
import com.kantek.coroutines.R
import com.kantek.coroutines.exceptions.AlertException
import com.kantek.coroutines.exceptions.ResourceException
import com.kantek.coroutines.exceptions.SnackException
import com.kantek.coroutines.repository.LoginRepository

class LoginViewModel(private val loginRepository: LoginRepository) : BaseViewModel() {
    val form = SingleLiveEvent<Pair<String?, String?>>()

    val user = form.mapLaunch(this) {
        if (it == null) throw ResourceException(R.string.error_login_form)
        if (it.first.isNullOrEmpty()) throw AlertException(R.string.error_user_name)
        if (it.second.isNullOrEmpty()) throw SnackException(R.string.error_password)
        loginRepository.login(it)
    }
}