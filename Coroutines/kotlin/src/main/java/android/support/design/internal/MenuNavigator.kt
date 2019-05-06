package android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.R
import android.support.annotation.IdRes
import android.support.annotation.NonNull
import android.support.core.base.BaseFragment
import android.support.core.extensions.addArgs
import android.support.core.functional.navigableOptions
import android.support.design.widget.MenuHostFragment
import android.support.design.widget.SupportNavHostFragment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.AttributeSet
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment

const val TYPE_ATTACH = 1
const val TYPE_KEEP_STATE = 2

abstract class MenuNavigator(private val containerId: Int, private val fragmentManager: FragmentManager) :
    Navigator<MenuNavigator.Destination>() {
    private var mCurTransaction: FragmentTransaction? = null
    private var mCurrentDestination: Destination? = null
    private lateinit var mOnNavigateChangedListener: (Int) -> Unit

    val currentDestination get() = mCurrentDestination
    val currentTransaction get() = mCurTransaction

    companion object {
        fun create(containerId: Int, fragmentManager: FragmentManager, navigationType: Int): MenuNavigator {
            return if (navigationType == TYPE_KEEP_STATE)
                KeepStateNavigator(containerId, fragmentManager)
            else AttachNavigator(containerId, fragmentManager)
        }
    }

    override fun createDestination() = Destination(this)

    override fun popBackStack(): Boolean {
        return false
    }

    override fun navigate(destination: Destination,
                          args: Bundle?,
                          navOptions: NavOptions?,
                          navigatorExtras: Extras?): NavDestination? {
        if (mCurrentDestination?.id == destination.id) {
            if (args != null) notifyArgumentsChanged(destination, args)
            return destination
        }
        startUpdate()
        if (mCurrentDestination == null) hideFragmentsIfNeeded()
        if (navOptions != null && mCurTransaction != null)
            addAnimationIfNeeded(navOptions, destination.order - (mCurrentDestination?.order
                ?: -1) < 0)
        instantiate(destination, args)
        mCurrentDestination?.let { destroy(it) }
        finishUpdate()
        mCurrentDestination = destination
        mOnNavigateChangedListener(destination.id)
        return destination
    }

    fun navigate(hostDestination: Destination, @IdRes childDestination: Int, args: Bundle? = null, navOptions: NavOptions? = null): NavDestination? {
        return navigate(hostDestination, navigableOptions(childDestination, args), navOptions, null)
    }

    private fun notifyArgumentsChanged(destination: Destination, args: Bundle) {
        when (val fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, destination.id))) {
            is BaseFragment -> {
                fragment.arguments = args
                fragment.handleNavigateArguments(args)
            }
            is MenuHostFragment -> {
                fragment.addArgs(args)
                fragment.navigateIfNeeded()
            }
            is SupportNavHostFragment -> {
                fragment.addArgs(args)
                fragment.navigateIfNeeded()
            }
        }
    }

    private fun addAnimationIfNeeded(navOptions: NavOptions, shouldRevertAnim: Boolean) {
        var enterAnim = navOptions.enterAnim
        var exitAnim = navOptions.exitAnim
        var popEnterAnim = navOptions.popEnterAnim
        var popExitAnim = navOptions.popExitAnim
        enterAnim = if (enterAnim != -1) enterAnim else 0
        exitAnim = if (exitAnim != -1) exitAnim else 0
        popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
        popExitAnim = if (popExitAnim != -1) popExitAnim else 0
        if (shouldRevertAnim)
            mCurTransaction!!.setCustomAnimations(popEnterAnim, popExitAnim, enterAnim, exitAnim)
        else
            mCurTransaction!!.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    private fun hideFragmentsIfNeeded() {
        for (it in fragmentManager.fragments) {
            if (it.isAdded && !it.isHidden && it.userVisibleHint)
                hideFragment(it)
        }
    }

    private fun makeFragmentName(viewId: Int, id: Int) = "android:switcher:$viewId:$id"

    private fun createFragmentIfNeeded(destination: Destination): Fragment {
        var fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, destination.id))
        if (fragment == null)
            fragment = destination.createFragment()
        return fragment
    }

    private fun destroy(from: Destination) {
        val fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, from.id))
        hideFragment(fragment!!)
    }

    private fun instantiate(destination: Destination, args: Bundle?) {
        var fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, destination.id))
        if (fragment != null) {
            showFragment(fragment)
        } else {
            fragment = createFragmentIfNeeded(destination)
            mCurTransaction!!.add(containerId, fragment, makeFragmentName(containerId, destination.id))
        }
        mCurTransaction!!.setPrimaryNavigationFragment(fragment)

        when (fragment) {
            is MenuHostFragment -> if (args != null) fragment.addArgs(args)
            is NavHostFragment -> if (args != null) fragment.addArgs(args)
            else -> fragment.arguments = args
        }
    }

    abstract fun hideFragment(fragment: Fragment)

    abstract fun showFragment(fragment: Fragment)

    private fun finishUpdate() {
        if (mCurTransaction != null) {
            mCurTransaction!!.commitNowAllowingStateLoss()
            mCurTransaction = null
        }
    }

    @SuppressLint("CommitTransaction")
    private fun startUpdate() {
        if (mCurTransaction == null) {
            mCurTransaction = fragmentManager.beginTransaction()
        }
    }

    fun setOnNavigateChangedListener(function: (Int) -> Unit) {
        mOnNavigateChangedListener = function
    }

    @NavDestination.ClassType(Fragment::class)
    class Destination : NavDestination {

        private var mFragmentClass: Class<out Fragment>? = null
        private var mNavGraph = 0
        var order = 0
            private set

        val fragmentClass: Class<out Fragment>?
            @NonNull
            get() {
                if (mFragmentClass == null)
                    throw IllegalStateException("fragment class not set")

                return mFragmentClass
            }

        constructor(@NonNull navigatorProvider: NavigatorProvider)
                : super(navigatorProvider.getNavigator(MenuNavigator::class.java)
        )

        constructor(@NonNull fragmentNavigator: Navigator<out Destination>) : super(fragmentNavigator)

        override fun onInflate(@NonNull context: Context, @NonNull attrs: AttributeSet) {
            super.onInflate(context, attrs)
            val a = context.resources.obtainAttributes(attrs, R.styleable.FragmentNavigator)
            val className = a.getString(R.styleable.FragmentNavigator_android_name)
            if (className != null) {
                setFragmentClass(parseClassFromName(context, className, Fragment::class.java))
            }
            a.recycle()
            val graph = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
            mNavGraph = graph.getResourceId(R.styleable.NavHostFragment_navGraph, 0)
            graph.recycle()

            val order = context.obtainStyledAttributes(attrs, R.styleable.Destination)
            this.order = graph.getInteger(R.styleable.Destination_navOrder, 0)
            order.recycle()
        }

        @NonNull
        fun setFragmentClass(@NonNull clazz: Class<out Fragment>): Destination {
            mFragmentClass = clazz
            return this
        }

        @NonNull
        fun createFragment(): Fragment {
            val clazz = fragmentClass
            val f: Fragment
            try {
                f = when {
                    NavHostFragment::class.java.isAssignableFrom(clazz!!) -> {
                        if (mNavGraph == 0) throw RuntimeException("Need a navGraph for host")
                        SupportNavHostFragment.create(mNavGraph)
                    }
                    MenuHostFragment::class.java.isAssignableFrom(clazz) -> {
                        if (mNavGraph == 0) throw RuntimeException("Need a navGraph for host")
                        MenuHostFragment.create(mNavGraph)
                    }
                    else -> clazz.newInstance()
                }
            } catch (e: Exception) {
                throw RuntimeException(e)
            }

            return f
        }
    }

    @Navigator.Name("fragment")
    private class AttachNavigator(containerId: Int, fragmentManager: FragmentManager) :
        MenuNavigator(containerId, fragmentManager) {
        override fun hideFragment(fragment: Fragment) {
            currentTransaction!!.detach(fragment)
        }

        override fun showFragment(fragment: Fragment) {
            currentTransaction!!.attach(fragment)
        }
    }

    @Navigator.Name("fragment")
    private class KeepStateNavigator(containerId: Int, fragmentManager: FragmentManager) :
        MenuNavigator(containerId, fragmentManager) {

        override fun hideFragment(fragment: Fragment) {
            currentTransaction!!.hide(fragment)
        }

        override fun showFragment(fragment: Fragment) {
            currentTransaction!!.show(fragment)
        }
    }
}