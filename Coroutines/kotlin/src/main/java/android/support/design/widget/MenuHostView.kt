package android.support.design.widget

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.content.Context
import android.support.annotation.NavigationRes
import android.support.core.base.BaseFragment
import android.support.core.functional.MenuOwner
import android.support.design.internal.MenuNavController
import android.support.design.internal.MenuNavigator
import android.support.design.internal.TYPE_KEEP_STATE
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.navigation.fragment.R

class MenuHostView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var mFragmentManager: FragmentManager? = null
    val navController = MenuNavController(context)
    private var mGraphId: Int = 0

    init {
        loadAttrs(attrs)
        setTag(android.support.R.string.nav_menu_controller_view_tag, navController)
        tag = android.support.R.string.nav_menu_controller_view_tag
    }

    @SuppressLint("CustomViewStyleable")
    private fun loadAttrs(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
        mGraphId = a.getResourceId(R.styleable.NavHostFragment_navGraph, 0)
        a.recycle()
        val ta = context.obtainStyledAttributes(attrs, android.support.R.styleable.MenuHostFragment)
        ta.recycle()
    }

    private fun setGraph(@NavigationRes graphResId: Int) {
        navController.setGraph(graphResId)
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        mFragmentManager = fragmentManager
        navController.navigatorProvider.addNavigator(MenuNavigator.create(id, fragmentManager, TYPE_KEEP_STATE))
        setGraph(mGraphId)
    }

    fun setupWithView(view: View, fragmentManager: FragmentManager) {
        mFragmentManager = fragmentManager
        val options = MenuNavController.animOptions()
        navController.navigatorProvider.addNavigator(MenuNavigator.create(id, fragmentManager, TYPE_KEEP_STATE))
        setGraph(mGraphId)
        if (view is MenuOwner) {
            view.setOnIdSelectedListener { navController.navigate(it, navOptions = options) }
            navController.setOnNavigateChangeListener { view.selectId(it) }
            navController.navigate(view.getCurrentId())
        } else {
            if (navController.getDestinationCount() < 2) throw RuntimeException("Navigation graph need 2 fragment to setup")
            val navigate: (Boolean) -> Unit = {
                val destination = navController.getDestinationActivated(view.isActivated)
                if (it) navController.navigate(destination, navOptions = options)
                else navController.navigate(destination)
            }
            navigate(false)
            view.setOnClickListener {
                view.isActivated = !view.isActivated
                navigate(true)
            }
        }
    }

    fun preformResume() {
        (mFragmentManager!!.primaryNavigationFragment as? BaseFragment)?.apply {
            viewLife.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    fun preformPause() {
        (mFragmentManager!!.primaryNavigationFragment as? BaseFragment)?.apply {
            viewLife.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
    }

    companion object {
        fun findNavController(fragment: Fragment): MenuNavController? {
            val view = fragment.view
            if (view != null) {
                val hostView = view.findViewWithTag<MenuHostView>(android.support.R.string.nav_menu_controller_view_tag)
                if (hostView != null) return hostView.navController
            }
            if (fragment.parentFragment != null) return findNavController(fragment.parentFragment!!)
            return null
        }
    }
}