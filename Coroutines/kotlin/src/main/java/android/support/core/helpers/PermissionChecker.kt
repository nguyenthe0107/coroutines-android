@file:Suppress("UNUSED")

package android.support.core.helpers

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.support.core.base.BaseActivity
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog


open class PermissionChecker(private val activity: BaseActivity) {
    companion object {
        const val REQUEST_PERMISSION_CHECKER = 10000
    }

    private var mOpenSettingDialog: AlertDialog? = null

    protected open var titleDenied = "Permission denied"
    protected open var messageDenied = "You need to allow permission to use this feature"

    protected fun access(vararg permissions: String, onAccess: () -> Unit) {
        check(*permissions) { if (it) onAccess() }
    }

    protected fun check(vararg permissions: String, onPermission: (Boolean) -> Unit) {
        if (permissions.isEmpty()) throw RuntimeException("No permission to check")
        if (isAllowed(*permissions)) onPermission(true) else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[0]))
                showSuggestOpenSetting(permissions, onPermission)
            else request(permissions, onPermission)
        }
    }

    private fun request(permissions: Array<out String>, onPermission: (Boolean) -> Unit) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION_CHECKER)
        activity.resultLife.onPermissionsResult { requestCodeReceived, _, grantResults ->
            if (REQUEST_PERMISSION_CHECKER != requestCodeReceived) return@onPermissionsResult
            if (grantResults.isEmpty()) {
                onPermission(false)
                return@onPermissionsResult
            }
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    onPermission(false)
                    return@onPermissionsResult
                }
            }
            onPermission(true)
        }
    }

    private fun isAllowed(vararg permissions: String): Boolean {
        return permissions.fold(true) { acc, permission ->
            acc && ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showSuggestOpenSetting(permissions: Array<out String>, onPermission: (Boolean) -> Unit) {
        if (mOpenSettingDialog == null) {
            mOpenSettingDialog = AlertDialog.Builder(activity)
                .setTitle(titleDenied)
                .setMessage(messageDenied)
                .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                    openSetting(permissions, onPermission)
                }
                .create()
        }
        mOpenSettingDialog!!.show()
    }

    private fun openSetting(permissions: Array<out String>, onPermission: (Boolean) -> Unit) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + activity.packageName))
        activity.startActivityForResult(intent, REQUEST_PERMISSION_CHECKER)
        activity.resultLife.onActivityResult { requestCodeReceived, _, _ ->
            if (REQUEST_PERMISSION_CHECKER != requestCodeReceived) return@onActivityResult
            onPermission(isAllowed(*permissions))
        }
    }
}
