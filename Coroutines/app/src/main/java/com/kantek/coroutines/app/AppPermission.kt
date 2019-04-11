package com.kantek.coroutines.app

import android.Manifest
import android.support.core.base.BaseActivity
import android.support.core.helpers.PermissionChecker

class AppPermission(activity: BaseActivity) : PermissionChecker(activity) {
    fun accessLocation(function: () -> Unit) {
        access(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, onAccess = function)
    }
}