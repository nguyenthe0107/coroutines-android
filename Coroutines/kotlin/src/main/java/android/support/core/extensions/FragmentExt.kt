package android.support.core.extensions

import android.annotation.SuppressLint
import android.support.v4.app.Fragment


private fun Fragment.isParentVisible(): Boolean {
    var parent = parentFragment
    while (parent != null) {
        if (parent.isHidden) return false
        parent = parent.parentFragment
    }
    return true
}

@SuppressLint("RestrictedApi")
fun Fragment.isVisibleOnScreen(): Boolean {
    return !isHidden && isParentVisible() && userVisibleHint
}

fun Fragment.dispatchHidden(hidden: Boolean) {
    val fragments = childFragmentManager.fragments
    for (fragment in fragments) {
        if (!fragment.isHidden && fragment.userVisibleHint)
            fragment.onHiddenChanged(hidden)
    }
}