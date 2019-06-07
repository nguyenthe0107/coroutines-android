package android.support.core.lifecycle

import android.app.Activity
import android.content.Intent

interface ResultLifecycle {
    fun onActivityResult(callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit)

    fun onActivitySuccessResult(callback: (data: Intent?) -> Unit)

    fun onPermissionsResult(callback: (requestCode: Int, permissions: Array<out String>, grantResults: IntArray) -> Unit)

}

class ResultRegistry : ResultLifecycle {

    private val mPermissions = hashSetOf<(Int, Array<out String>, IntArray) -> Unit>()
    private val mActivityResults = hashSetOf<(Int, Int, Intent?) -> Unit>()
    private val mActivitySuccessResults = hashSetOf<(Intent?) -> Unit>()

    override fun onPermissionsResult(callback: (requestCode: Int, permissions: Array<out String>, grantResults: IntArray) -> Unit) {
        mPermissions.add(callback)
    }

    override fun onActivitySuccessResult(callback: (data: Intent?) -> Unit) {
        mActivitySuccessResults.add(callback)
    }

    override fun onActivityResult(callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {
        mActivityResults.add(callback)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mActivityResults.forEach { it(requestCode, resultCode, data) }
        if (resultCode == Activity.RESULT_OK) mActivitySuccessResults.forEach { it(data) }
        mActivityResults.clear()
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissions.forEach { it(requestCode, permissions, grantResults) }
        mPermissions.clear()
    }
}