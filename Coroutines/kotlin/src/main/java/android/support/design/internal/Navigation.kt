package android.support.design.internal

import android.app.Activity
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.MenuHostFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHost
import androidx.navigation.R
import androidx.navigation.fragment.NavHostFragment
import java.lang.ref.WeakReference

/**
 * Entry point for navigation operations.
 *
 * <p>This class provides utilities for finding a relevant {@link NavController} instance from
 * various common places in your application, or for performing navigation in response to
 * UI events.</p>
 */
object Navigation {
    /**
     * Find a [NavController] given the id of a View and its containing
     * [Activity]. This is a convenience wrapper around [.findNavController].
     *
     *
     * This method will locate the [NavController] associated with this view.
     * This is automatically populated for the id of a [NavHost] and its children.
     *
     * @param activity The Activity hosting the view
     * @param viewId The id of the view to search from
     * @return the [NavController] associated with the view referenced by id
     * @throws IllegalStateException if the given viewId does not correspond with a
     * [NavHost] or is not within a NavHost.
     */
    fun findNavController(activity: Activity, @IdRes viewId: Int): NavController {
        val view = ActivityCompat.requireViewById<View>(activity, viewId)
        return findViewNavController(view) ?: throw IllegalStateException("Activity " + activity
                + " does not have a NavController set on " + viewId)
    }

    /**
     * Find a [NavController] given a local [View].
     *
     *
     * This method will locate the [NavController] associated with this view.
     * This is automatically populated for views that are managed by a [NavHost]
     * and is intended for use by various [listener][android.view.View.OnClickListener]
     * interfaces.
     *
     * @param view the view to search from
     * @return the locally scoped [NavController] to the given view
     * @throws IllegalStateException if the given view does not correspond with a
     * [NavHost] or is not within a NavHost.
     */
    fun findNavController(view: View) = findViewNavController(view)

    /**
     * Find a [NavController] given a local [Fragment].
     *
     *
     * This method will locate the [NavController] associated with this fragment.
     *
     * @param fragment the fragment to scan at the content of this
     * @return the locally scoped [NavController] to the given fragment
     * @throws IllegalStateException if the given view does not correspond with a
     * [NavHost] or is not within a NavHost.
     */
    fun findNavController(fragment: Fragment): NavController? {
        var findFragment: Fragment? = fragment
        while (findFragment != null) {
            if (findFragment is NavHostFragment) {
                return findFragment.navController
            }
            val primaryNavFragment = findFragment.requireFragmentManager().primaryNavigationFragment
            if (primaryNavFragment is NavHostFragment) return primaryNavFragment.navController
            findFragment = findFragment.parentFragment
        }

        val view = fragment.view
        if (view != null) {
            return findNavController(view)
        }
        return null
    }

    fun findMenuNavController(fragment: Fragment): MenuNavController? {
        var findFragment: Fragment? = fragment
        while (findFragment != null) {
            if (findFragment is MenuHostFragment) {
                return findFragment.navController
            }
            val primaryNavFragment = findFragment.requireFragmentManager().primaryNavigationFragment
            if (primaryNavFragment is MenuHostFragment) return primaryNavFragment.navController
            findFragment = findFragment.parentFragment
        }

        val view = fragment.view
        if (view != null) {
            return findMenuNavController(view)
        }
        return null
    }

    private fun findMenuNavController(view: View?): MenuNavController? {
        var tmpView = view
        while (tmpView != null) {
            val controller = getMenuNavController(tmpView)
            if (controller != null) {
                return controller
            }
            val parent = tmpView.parent
            tmpView = if (parent is View) parent else null
        }
        return null
    }

    /**
     * Create an [android.view.View.OnClickListener] for navigating
     * to a destination. This supports both navigating via an
     * [action][NavDestination.getAction] and directly navigating to a destination.
     *
     * @param resId an [action][NavDestination.getAction] id or a destination id to
     * navigate to when the view is clicked
     * @return a new click listener for setting on an arbitrary view
     */
    fun createNavigateOnClickListener(@IdRes resId: Int): View.OnClickListener {
        return createNavigateOnClickListener(resId, null)
    }

    /**
     * Create an [android.view.View.OnClickListener] for navigating
     * to a destination. This supports both navigating via an
     * [action][NavDestination.getAction] and directly navigating to a destination.
     *
     * @param resId an [action][NavDestination.getAction] id or a destination id to
     * navigate to when the view is clicked
     * @param args arguments to pass to the final destination
     * @return a new click listener for setting on an arbitrary view
     */
    fun createNavigateOnClickListener(@IdRes resId: Int,
                                      args: Bundle?): View.OnClickListener {
        return View.OnClickListener { view -> findNavController(view)?.navigate(resId, args) }
    }

    /**
     * Associates a NavController with the given View, allowing developers to use
     * [.findNavController] and [.findNavController] with that
     * View or any of its children to retrieve the NavController.
     *
     *
     * This is generally called for you by the hosting [NavHost].
     * @param view View that should be associated with the given NavController
     * @param controller The controller you wish to later retrieve via
     * [.findNavController]
     */
    fun setViewNavController(view: View,
                             controller: NavController?) {
        view.setTag(R.id.nav_controller_view_tag, controller)
    }

    /**
     * Recurse up the view hierarchy, looking for the NavController
     * @param view the view to search from
     * @return the locally scoped [NavController] to the given view, if found
     */
    private fun findViewNavController(view: View?): NavController? {
        var tmpView = view
        while (tmpView != null) {
            val controller = getViewNavController(tmpView)
            if (controller != null) {
                return controller
            }
            val parent = tmpView.parent
            tmpView = if (parent is View) parent else null
        }
        return null
    }

    private fun getViewNavController(view: View): NavController? {
        val tag = view.getTag(R.id.nav_controller_view_tag)
        var controller: NavController? = null
        if (tag is WeakReference<*>) {
            controller = tag.get() as? NavController
        } else if (tag is NavController) {
            controller = tag
        }
        return controller
    }

    private fun getMenuNavController(view: View): MenuNavController? {
        val tag = view.getTag(android.support.R.string.nav_menu_controller_view_tag)
        var controller: MenuNavController? = null
        if (tag is WeakReference<*>) {
            controller = tag.get() as? MenuNavController
        } else if (tag is MenuNavController) {
            controller = tag
        }
        return controller
    }
}