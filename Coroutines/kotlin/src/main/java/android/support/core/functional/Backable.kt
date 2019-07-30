package android.support.core.functional

import android.support.core.extensions.findChildVisible
import android.support.design.widget.MenuHostFragment
import androidx.fragment.app.Fragment

interface Backable {
    fun onBackPressed(): Boolean {
        if (this !is Fragment) return false
        val child = findChildVisible()
        if (child is Backable && child.onBackPressed()) return true
        if (this is MenuHostFragment && navController!!.navigateUp())
            return true
        return false
    }
}
