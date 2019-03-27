package com.kantek.coroutines.app

import android.support.annotation.StringRes
import android.support.core.base.BaseViewModel

abstract class AppViewModel : BaseViewModel() {

    fun error(@StringRes res: Int): Nothing = throw ResourceException(res)

    fun error(text: String): Nothing = throw Throwable(text)

    fun alert(@StringRes res: Int): Nothing = throw AlertException(res)

    fun snack(@StringRes res: Int): Nothing = throw SnackException(res)
}