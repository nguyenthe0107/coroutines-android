package android.support.core.functional

import android.support.core.extensions.findChildVisible
import android.support.design.internal.MenuStackNavigator
import android.support.design.widget.MenuHostFragment
import android.support.v4.app.Fragment

interface Backable {
    fun onBackPressed(): Boolean {
        if (this is Fragment && (findChildVisible() as? Backable)?.onBackPressed() == true) return true

        if (this is MenuHostFragment && navController!!.navigator is MenuStackNavigator)
            return navController!!.navigateUp()

        return false
    }
}
