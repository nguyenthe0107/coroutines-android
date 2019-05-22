package android.support.design.internal

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.NonNull
import androidx.navigation.*

@SuppressLint("RestrictedApi")
class MenuNavController(context: Context) {
    private lateinit var mNavGraph: NavGraph
    private var mNavGraphId: Int = 0
    private var mOnNavigatorChangedListener: ((Int) -> Unit)? = null
    private lateinit var mNavigator: MenuNavigator

    val navigatorProvider = object : NavigatorProvider() {
        override fun addNavigator(name: String, navigator: Navigator<out NavDestination>): Navigator<out NavDestination>? {
            if (navigator is MenuNavigator) {
                mNavigator = navigator
                navigator.setOnNavigateChangedListener { mOnNavigatorChangedListener?.invoke(it) }
            }
            return super.addNavigator(name, navigator)
        }
    }
    private val mInflater: NavInflater = NavInflater(context, navigatorProvider)

    init {
        navigatorProvider.addNavigator(NavGraphNavigator(navigatorProvider))
    }

    fun navigate(@IdRes hostId: Int, @IdRes childId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        mNavigator.navigate(mNavGraph.findDestination(hostId), childId, args, navOptions)
    }

    fun navigate(@NonNull directions: NavDirections) {
        navigate(directions.actionId, directions.arguments)
    }

    fun navigate(@IdRes id: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        if (mNavGraphId == 0) throw RuntimeException("Not set fragment manager yet!")
        var desId = id
        var navOpts = navOptions
        val arguments = mNavigator.currentDestination?.getAction(id)?.let {
            desId = it.destinationId
            if (navOpts == null) navOpts = it.navOptions
            if (it.defaultArguments != null) Bundle().also { combine ->
                combine.putAll(it.defaultArguments)
                if (args != null) combine.putAll(args)
            } else args
        } ?: args
        navigate(mNavGraph.findDestination(desId), arguments, navOpts)
    }

    fun navigate(destination: MenuNavigator.Destination, args: Bundle? = null, navOptions: NavOptions? = null) {
        mNavigator.navigate(destination, args, navOptions, null)
    }

    fun navigateToStart() {
        mNavigator.navigate(mNavGraph.findDestination(mNavGraph.startDestination), null, null, null)
    }

    fun setOnNavigateChangeListener(function: (Int) -> Unit) {
        mOnNavigatorChangedListener = function
    }

    fun getDestinationActivated(activated: Boolean): MenuNavigator.Destination {
        return if (!activated) mNavGraph.findDestination(mNavGraph.startDestination)
        else mNavGraph.find { it.id != mNavGraph.startDestination } as MenuNavigator.Destination
    }

    fun getActivated(@IdRes desId: Int) = mNavGraph.startDestination != desId

    fun getDestinationCount(): Int {
        return mNavGraph.count()
    }

    fun setGraph(graphResId: Int) {
        mNavGraphId = graphResId
        mNavGraph = mInflater.inflate(graphResId)
    }

    fun restoreState(navState: Bundle?) {
        if (navState == null) return
        mNavGraphId = navState.getInt(KEY_GRAPH_ID)
        if (mNavGraphId != 0) setGraph(mNavGraphId)
    }

    fun saveState(): Bundle? {
        var b: Bundle? = null
        if (mNavGraphId != 0) {
            b = Bundle()
            b.putInt(KEY_GRAPH_ID, mNavGraphId)
        }
        return b
    }

    fun navigateUp(): Boolean {
        navigateToStart()
        return true
    }

    companion object {
        fun animOptions() = NavOptions.Builder()
            .setEnterAnim(android.support.R.anim.default_fade_in)
            .setExitAnim(android.support.R.anim.default_fade_out)
            .setPopEnterAnim(android.support.R.anim.default_fade_in)
            .setPopExitAnim(android.support.R.anim.default_fade_out)
            .build()

        private const val KEY_GRAPH_ID = "android-support-nav:controller:graphId"
    }

}