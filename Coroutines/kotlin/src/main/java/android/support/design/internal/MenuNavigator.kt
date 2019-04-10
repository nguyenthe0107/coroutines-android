package android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.R
import android.support.annotation.IdRes
import android.support.annotation.NonNull
import android.support.core.base.BaseFragment
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

    val currentTransaction: FragmentTransaction?
        get() = mCurTransaction

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

    override fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Extras?): NavDestination? {
        return navigate(destination, args, navOptions)
    }

    fun navigate(hostDestination: Destination, @IdRes childDestination: Int, args: Bundle?, navOptions: NavOptions?): NavDestination? {
        return navigate(hostDestination, Bundle().apply {
            putInt(MenuHostFragment.KEY_NAVIGATE_CHILD_ID, childDestination)
            putBundle(MenuHostFragment.KEY_NAVIGATE_ARGS, args)
        }, navOptions)
    }

    fun navigate(destination: Destination) {
        navigate(destination, null)
    }

    fun navigate(destination: Destination, navOptions: NavOptions?) {
        navigate(destination, null, navOptions)
    }

    fun navigate(destination: Destination, args: Bundle?, navOptions: NavOptions?): NavDestination? {
        if (mCurrentDestination?.id == destination.id) {
            if (args != null) notifyArgumentsChanged(destination, args)
            return destination
        }
        startUpdate()
        if (mCurrentDestination == null) hideFragmentsIfNeeded()
        addAnimationIfNeeded(navOptions)
        instantiate(destination, args)
        mCurrentDestination?.let { destroy(it) }
        finishUpdate()
        mCurrentDestination = destination
        mOnNavigateChangedListener(destination.id)
        return destination
    }

    private fun notifyArgumentsChanged(destination: Destination, args: Bundle) {
        val fragment = fragmentManager.findFragmentByTag(makeFragmentName(containerId, destination.id))
        when (fragment) {
            is BaseFragment -> {
                fragment.arguments = args
                fragment.onNavigateArguments(args)
            }
            is MenuHostFragment -> {
                fragment.addArgs(args)
                fragment.handleArguments()
            }
        }
    }

    private fun addAnimationIfNeeded(navOptions: NavOptions?) {
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            mCurTransaction?.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
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
            is NavHostFragment -> if (args != null) throw IllegalArgumentException("Not support by pass arguments for NavHostFragment")
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

        constructor(@NonNull fragmentNavigator: Navigator<out MenuNavigator.Destination>) : super(fragmentNavigator)

        override fun onInflate(@NonNull context: Context, @NonNull attrs: AttributeSet) {
            super.onInflate(context, attrs)
            val a = context.resources.obtainAttributes(
                attrs, R.styleable.FragmentNavigator
            )
            val className = a.getString(R.styleable.FragmentNavigator_android_name)
            if (className != null) {
                setFragmentClass(NavDestination.parseClassFromName(context, className, Fragment::class.java))
            }
            a.recycle()
            val graph = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
            mNavGraph = graph.getResourceId(R.styleable.NavHostFragment_navGraph, 0)
            graph.recycle()
        }

        @NonNull
        fun setFragmentClass(@NonNull clazz: Class<out Fragment>): MenuNavigator.Destination {
            mFragmentClass = clazz
            return this
        }

        @NonNull
        fun createFragment(): Fragment {
            val clazz = fragmentClass
            val f: Fragment
            try {
                f = when {
                    clazz!!.isAssignableFrom(NavHostFragment::class.java) -> {
                        if (mNavGraph == 0) throw RuntimeException("Need a navGraph for host")
                        SupportNavHostFragment.create(mNavGraph)
                    }
                    clazz.isAssignableFrom(MenuHostFragment::class.java) -> {
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