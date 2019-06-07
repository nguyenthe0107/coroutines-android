package com.android.navigator

import android.support.core.extensions.findMenuNavController
import android.support.core.functional.Backable
import android.support.v4.app.Fragment

abstract class BaseFragment : Fragment(), Backable {
    override fun onBackPressed() = findMenuNavController().navigateUp()
}