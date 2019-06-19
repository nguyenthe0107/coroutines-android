package android.support.design.internal

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import java.io.Serializable

fun NavController.isCurrentAsStart(): Boolean {
    val navDestination = currentDestination ?: return true
    return navDestination.id == graph.startDestination
}

fun NavGraph.findStartDestination() = findDestination(startDestination)

fun NavGraph.findDestination(id: Int): MenuNavigator.Destination =
        findNode(id) as MenuNavigator.Destination?
                ?: throw RuntimeException("No fragment destination match id $id ")

fun Fragment.addBundle(key: String, bundle: Bundle) {
    var args = arguments
    if (args == null) args = Bundle()
    args.putBundle(key, bundle)
    arguments = args
}

fun Bundle.withs(vararg pairs: Pair<String, Any>): Bundle? {
    pairs.forEach {
        when (it.second) {
            is Serializable -> putSerializable(it.first, it.second as Serializable)
            is Parcelable -> putParcelable(it.first, it.second as Parcelable)
        }
    }
    return this
}
