package android.support.design.internal

import android.content.Context
import android.support.R
import android.support.annotation.NonNull
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.AttributeSet
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

@Navigator.Name("fragment")
class MenuOrderNavigator(private val containerId: Int, fragmentManager: FragmentManager)
    : MenuNavigator(fragmentManager) {

    override fun findFragment(destinationId: Int): Fragment? {
        return fragmentManager.findFragmentByTag(makeFragmentName(containerId, destinationId))
    }

    override fun instantiate(transaction: FragmentTransaction, destination: MenuNavigator.Destination, navOptions: NavOptions?): Fragment {
        var fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, destination.id))
        if (fragment != null) {
            transaction.show(fragment)
        } else {
            fragment = destination.createFragment()
            transaction.add(containerId, fragment, makeFragmentName(containerId, destination.id))
        }
        return fragment
    }

    private fun makeFragmentName(viewId: Int, id: Int) = "android:switcher:$viewId:$id"

    override fun createDestination() = Destination(this)

    override fun setCustomAnimations(destination: MenuNavigator.Destination?, enterAnim: Int, exitAnim: Int, popEnterAnim: Int, popExitAnim: Int) {
        destination as Destination
        val shouldReverse = destination.before(currentDestination)
        if (!shouldReverse)
            super.setCustomAnimations(destination, enterAnim, exitAnim, popEnterAnim, popExitAnim)
        else super.setCustomAnimations(destination, popEnterAnim, popExitAnim, enterAnim, exitAnim)
    }

    @NavDestination.ClassType(Fragment::class)
    class Destination(@NonNull fragmentNavigator: Navigator<out MenuNavigator.Destination>) : MenuNavigator.Destination(fragmentNavigator) {

        var order = 0
            private set

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            val order = context.obtainStyledAttributes(attrs, R.styleable.Destination)
            this.order = order.getInteger(R.styleable.Destination_navOrder, 0)
            order.recycle()
        }

        fun before(destination: MenuNavigator.Destination?) =
            order - ((destination as? Destination)?.order ?: -1) < 0
    }
}