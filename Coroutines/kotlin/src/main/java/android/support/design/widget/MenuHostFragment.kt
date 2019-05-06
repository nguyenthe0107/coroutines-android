package android.support.design.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.NavigationRes
import android.support.core.extensions.addArgs
import android.support.core.extensions.dispatchHidden
import android.support.core.extensions.isVisibleOnScreen
import android.support.core.functional.MenuOwner
import android.support.core.functional.navigateIfNeeded
import android.support.design.internal.MenuNavController
import android.support.design.internal.MenuNavigator
import android.support.design.internal.TYPE_KEEP_STATE
import android.support.v4.app.Fragment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.NavOptions
import androidx.navigation.fragment.R

class MenuHostFragment : Fragment() {
    var navController: MenuNavController? = null
        private set
    private var mOnActivityCreatedListener: (() -> Unit)? = null
    private var mNavOptions: NavOptions? = null

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
        return FrameLayout(inflater.context).also { it.id = id }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view !is ViewGroup) throw IllegalStateException("created host view $view is not a ViewGroup")
        val rootView = if (view.getParent() != null) view.getParent() as View else view
        rootView.setTag(android.support.R.string.nav_menu_controller_view_tag, navController!!)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments == null) {
            navController!!.navigateToStart()
            return
        }

        val menuId = arguments!!.getInt(KEY_MENU_ID, 0)
        if (menuId != 0) setMenu(menuId)
        if (navigateIfNeeded()) return
        if (menuId != 0) mOnActivityCreatedListener?.invoke() else navController!!.navigateToStart()
    }

    fun navigateIfNeeded() =
        navigateIfNeeded { childId, navArgs -> navController!!.navigate(childId, navArgs) }

    private fun setMenu(menuId: Int) {
        val rootView = if (view!!.parent != null) view!!.parent as View else view!!
        val view = rootView.findViewById<View>(menuId)
        setupWithView(view)
    }

    override fun onInflate(context: Context?, attrs: AttributeSet?, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        val a = context!!.obtainStyledAttributes(attrs, R.styleable.NavHostFragment)
        val graphId = a.getResourceId(R.styleable.NavHostFragment_navGraph, 0)
        a.recycle()
        val ta = context.obtainStyledAttributes(attrs, android.support.R.styleable.MenuHostFragment)
        val menuId = ta.getResourceId(android.support.R.styleable.MenuHostFragment_navMenu, 0)
        ta.recycle()

        val action = context.resources.obtainAttributes(attrs, R.styleable.NavAction)
        mNavOptions = NavOptions.Builder()
            .setEnterAnim(a.getResourceId(R.styleable.NavAction_enterAnim, android.support.R.anim.default_fade_in))
            .setExitAnim(a.getResourceId(R.styleable.NavAction_exitAnim, android.support.R.anim.default_fade_out))
            .setPopEnterAnim(a.getResourceId(R.styleable.NavAction_popEnterAnim, android.support.R.anim.default_fade_in))
            .setPopExitAnim(a.getResourceId(R.styleable.NavAction_popExitAnim, android.support.R.anim.default_fade_out))
            .build()
        action.recycle()

        if (graphId != 0) setGraph(graphId, menuId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navController!!.saveState()?.apply {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, this)
        }
    }

    fun setGraph(@NavigationRes graphResId: Int, menuId: Int) {
        if (navController == null) addArgs(KEY_GRAPH_ID to graphResId, KEY_MENU_ID to menuId)
        else navController!!.setGraph(graphResId)
    }

    fun setupWithView(view: View) {
        if (mNavOptions == null) mNavOptions = MenuNavController.animOptions()
        if (view is MenuOwner) {
            mOnActivityCreatedListener = { navController!!.navigate(view.getCurrentId()) }
            view.setOnIdSelectedListener { navController!!.navigate(it, navOptions = mNavOptions) }
            navController!!.setOnNavigateChangeListener { view.selectId(it) }
        } else {
            if (navController!!.getDestinationCount() < 2) throw RuntimeException("Navigation graph need 2 fragment to setup")
            mOnActivityCreatedListener = {
                val destination = navController!!.getDestinationActivated(!view.isActivated)
                navController!!.navigate(destination, navOptions = mNavOptions)
            }
            view.setOnClickListener {
                view.isActivated = !view.isActivated
                mOnActivityCreatedListener!!()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isVisibleOnScreen()) navigateIfNeeded()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) navigateIfNeeded()
        dispatchHidden(hidden)
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
            val parent = fragment.parentFragment
            if (parent != null) return findNavController(parent)
            return null
        }

        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"
        private const val KEY_MENU_ID = "android-support-nav:fragment:menuId"
        private const val KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState"
    }
}