package android.support.design.internal

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.io.Serializable
import java.util.*

@Navigator.Name("fragment")
class MenuStackNavigator(private val containerId: Int,
                         fragmentManager: FragmentManager) : MenuNavigator(fragmentManager) {
    companion object {
        private const val KEY_STACK = "android:menu:stack"
    }

    private var mStack = Stack<DestinationWrapper>()

    override fun onSaveState(): Bundle? {
        val state = super.onSaveState()!!
        state.putSerializable(KEY_STACK, mStack)
        return state
    }

    @Suppress("unchecked_cast")
    override fun onRestoreState(savedState: Bundle) {
        mStack = savedState.getSerializable(KEY_STACK) as Stack<DestinationWrapper>
        super.onRestoreState(savedState)
    }

    override fun findFragment(destinationId: Int): Fragment? {
        val tag = mStack.findLast { it.destinationId == destinationId }?.fragmentTag ?: return null
        return findFragment(tag)
    }

    private fun findFragment(fragmentTag: String): Fragment? {
        return fragmentManager.findFragmentByTag(fragmentTag)
    }

    override fun instantiate(transaction: FragmentTransaction, destination: Destination, navOptions: NavOptions?): Fragment {
        val fragment: Fragment
        if (navOptions == null) {
            fragment = destination.createFragment()
            val tag = generateTag(fragment)
            transaction.add(containerId, fragment, tag)
            mStack.push(DestinationWrapper(destination.id, tag))
        } else {
            fragment = if (navOptions.shouldLaunchSingleTop()) findFragment(destination.id)
                ?: destination.createFragment()
            else destination.createFragment()

            if (navOptions.popUpTo != -1) {
                mStack.popUntil(navOptions.popUpTo, navOptions.isPopUpToInclusive) {
                    if (destinationId != destination.id
                        || !navOptions.shouldLaunchSingleTop()
                    ) transaction.remove(it)
                }
            }
            var tag = generateTag(fragment)
            if (!fragment.isAdded) transaction.add(containerId, fragment, tag)
            else {
                tag = fragment.tag!!
                transaction.show(fragment)
            }
            mStack.push(DestinationWrapper(destination.id, tag).setOptions(navOptions))
        }
        return fragment
    }

    private fun Stack<DestinationWrapper>.popUntil(
        popUpTo: Int,
        popUpToInclusive: Boolean,
        function: DestinationWrapper.(Fragment) -> Unit) {
        val accept = {
            val top = pop()
            function(top, findFragment(top.fragmentTag)!!)
        }
        while (true) {
            if (empty()) return
            val element = lastElement()
            if (element.destinationId == popUpTo) {
                if (popUpToInclusive) accept()
                return
            }
            accept()
        }
    }

    private fun generateTag(fragment: Fragment) =
        "android:switcher:${fragment.javaClass.simpleName}:${System.currentTimeMillis()}"

    override fun popBackStack(): Boolean {
        if (mStack.isEmpty()) return false
        val current = mStack.pop() ?: return false
        if (mStack.isEmpty()) return false
        val target = mStack.lastElement()
        transaction {
            addAnimationIfNeeded(current.makePopNavOptions(), null)
            remove(findFragment(current.fragmentTag)!!)
            show(findFragment(target.fragmentTag)!!)
        }
        notifyNavigateChanged(target.destinationId)
        return true
    }

    private class DestinationWrapper(val destinationId: Int,
                                     val fragmentTag: String) : Serializable {
        var mAnimEnter: Int = 0
        var mAnimExit: Int = 0
        var mAnimPopEnter: Int = 0
        var mAnimPopExit: Int = 0

        fun setOptions(value: NavOptions?): DestinationWrapper {
            mAnimEnter = value?.enterAnim ?: 0
            mAnimExit = value?.exitAnim ?: 0
            mAnimPopEnter = value?.popEnterAnim ?: 0
            mAnimPopExit = value?.popExitAnim ?: 0
            return this
        }

        fun makePopNavOptions(): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(mAnimPopEnter)
                .setExitAnim(mAnimPopExit)
                .setPopEnterAnim(mAnimEnter)
                .setPopExitAnim(mAnimExit)
                .build()
        }
    }
}
