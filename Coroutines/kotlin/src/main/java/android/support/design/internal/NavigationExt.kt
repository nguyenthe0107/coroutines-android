package android.support.design.internal

import androidx.navigation.NavController
import androidx.navigation.NavGraph

fun NavController.isCurrentAsStart(): Boolean {
    val navDestination = currentDestination ?: return true
    return navDestination.id == graph.startDestination
}

fun NavGraph.findDestination(id: Int): MenuNavigator.Destination =
        findNode(id) as MenuNavigator.Destination?
                ?: throw RuntimeException("No fragment destination match id $id ")
