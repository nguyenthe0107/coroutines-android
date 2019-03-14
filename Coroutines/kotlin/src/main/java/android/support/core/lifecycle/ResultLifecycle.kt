package android.support.core.lifecycle

import android.content.Intent
import android.support.core.functional.Backable

interface ResultLifecycle {
    fun onActivityResult(callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit)

    fun onPermissionsResult(callback: (requestCode: Int, permissions: Array<out String>, grantResults: IntArray) -> Unit)

}

class ResultRegistry : ResultLifecycle {
    private val mPermissions = hashSetOf<(Int, Array<out String>, IntArray) -> Unit>()
    private val mActivityResults = hashSetOf<(Int, Int, Intent?) -> Unit>()
    val backPresses = hashSetOf<Backable>()

    override fun onPermissionsResult(callback: (requestCode: Int, permissions: Array<out String>, grantResults: IntArray) -> Unit) {
        mPermissions.add(callback)
    }

    override fun onActivityResult(callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {
        mActivityResults.add(callback)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mActivityResults.forEach { it(requestCode, resultCode, data) }
        mActivityResults.clear()
    }

    fun handlePermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        mPermissions.forEach { it(requestCode, permissions, grantResults) }
        mPermissions.clear()
    }
}