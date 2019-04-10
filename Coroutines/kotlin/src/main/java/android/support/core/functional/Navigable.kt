package android.support.core.functional

import android.os.Bundle
import android.support.design.widget.MenuHostFragment

interface Navigable {
    fun handleNavigateArguments(args: Bundle) {
        if (args.containsKey(MenuHostFragment.KEY_NAVIGATE_CHILD_ID)) {
            val desId = args.getInt(MenuHostFragment.KEY_NAVIGATE_CHILD_ID)
            val byArgs = args.getBundle(MenuHostFragment.KEY_NAVIGATE_ARGS)
            navigateTo(desId, byArgs)
            args.remove(MenuHostFragment.KEY_NAVIGATE_CHILD_ID)
            args.remove(MenuHostFragment.KEY_NAVIGATE_ARGS)
        } else onNewArguments(args)
    }

    fun onNewArguments(args: Bundle) {
    }

    fun navigateTo(desId: Int, args: Bundle?) {
    }
}