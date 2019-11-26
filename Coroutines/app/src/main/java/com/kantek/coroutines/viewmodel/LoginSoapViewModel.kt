package com.kantek.coroutines.viewmodel

import android.support.core.event.SingleLiveEvent
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppViewModel
import com.kantek.coroutines.app.fail
import com.kantek.coroutines.datasource.SoapService
import com.kantek.coroutines.extensions.call
import com.kantek.coroutines.models.LoginRequest
import com.kantek.coroutines.models.LoginRequestBody
import com.kantek.coroutines.models.LoginRequestEnvelope

class LoginSoapViewModel(private val soapService: SoapService) : AppViewModel() {
    val form = SingleLiveEvent<Pair<String?, String?>>()

    val user = form.next {
        if (it == null) fail(R.string.error_login_form)
        soapService.login(
            LoginRequestEnvelope(
                LoginRequestBody(
                    LoginRequest(
                        "IOS_MOBILE_APP_API_3",
                        "#\$%wef45g43222rr**"
                    )
                )
            )
        ).call()
    }
}