package android.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.NavigationRes
import android.support.design.internal.MenuNavController
import android.support.design.internal.MenuNavigator
import android.support.design.internal.TYPE_KEEP_STATE
import android.support.core.functional.MenuOwner
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.R

class MenuHostFragment : Fragment() {
    var navController: MenuNavController? = null
        private set
    private var mOnActivityCreatedListener: (() -> Unit)? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = MenuNavController(context!!)
        navController!!.navigatorProvider.addNavigator(MenuNavigator.create(id, childFragmentManager, TYPE_KEEP_STATE))

        var navState: Bundle? = null
        if (savedInstanceState != null) navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE)
        if (navState != null) {
            navController!!.restoreState(navState)
        } else {
            val graphId = arguments?.getInt(KEY_GRAPH_ID) ?: 0
            if (graphId != 0) navController!!.setGraph(graphId)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val frameLayout = FrameLayout(inflater.context)
        frameLayout.id = id
        return frameLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view !is ViewGroup) throw IllegalStateException("created host view $view is not a ViewGroup")
        val rootView = if (view.getParent() != null) view.getParent() as View else view
        rootView.setTag(android.support.R.string.nav_menu_controller_view_tag, navController!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val menuId = arguments?.getInt(KEY_MENU_ID) ?: 0
        if (menuId != 0) {
            val rootView = if (view!!.parent != null) view!!.parent as View else view!!
            val view = rootView.findViewById<View>(menuId)
            setupWithView(view)
            mOnActivityCreatedListener?.invoke()
        } else {
            navController!!.navigateToStart()
        }
    }

    override fun onInflate(context: Context?, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
        val graphId = a.getResourceId(R.styleable.NavHostFragment_navGraph, 0)
        a.recycle()
        val ta = context.obtainStyledAttributes(attrs, android.support.R.styleable.MenuHostFragment)
        val menuId = ta.getResourceId(android.support.R.styleable.MenuHostFragment_navMenu, 0)
        ta.recycle()
        if (graphId != 0) setGraph(graphId, menuId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navController!!.saveState()?.apply {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, this)
        }
    }

    fun setGraph(@NavigationRes graphResId: Int, menuId: Int) {
        if (navController == null) {
            var args = arguments
            if (args == null) args = Bundle()
            args.putInt(KEY_GRAPH_ID, graphResId)
            args.putInt(KEY_MENU_ID, menuId)
            arguments = args
        } else {
            navController!!.setGraph(graphResId)
        }
    }

    fun setupWithView(view: View) {
        val navOptions = MenuNavController.animOptions()
        if (view is MenuOwner) {
            mOnActivityCreatedListener = {
                navController!!.navigate(view.getCurrentId())
            }
            view.setOnIdSelectedListener {
                navController!!.navigate(it, navOptions)
            }
            navController!!.setOnNavigateChangeListener { view.selectId(it) }
        } else {
            if (navController!!.getDestinationCount() < 2) throw RuntimeException("Navigation graph need 2 fragment to setup")
            val toggle = {
                val destination = navController!!.getDestinationActivated(!view.isActivated)
                navController!!.navigate(destination, navOptions)
            }
            mOnActivityCreatedListener = toggle
            view.setOnClickListener {
                view.isActivated = !view.isActivated
                toggle()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        val currentFragment = childFragmentManager.primaryNavigationFragment
        if (currentFragment != null) {
            currentFragment.onHiddenChanged(hidden)
        } else {
            childFragmentManager.fragments
                .find { !it.isHidden && it.userVisibleHint }
                ?.onHiddenChanged(hidden)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded) onHiddenChanged(!isVisibleToUser)
    }

    companion object {
        fun create(graphResId: Int): Fragment {
            val fragment = MenuHostFragment()
            fragment.setGraph(graphResId, 0)
            return fragment
        }

        fun findNavController(fragment: Fragment): MenuNavController? {
            if (fragment is MenuHostFragment) return fragment.navController
            var parent = fragment.parentFragment
            if (parent != null)
                return findNavController(parent)
            return null
        }

        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"
        private const val KEY_MENU_ID = "android-support-nav:fragment:menuId"
        private const val KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState"
    }
}