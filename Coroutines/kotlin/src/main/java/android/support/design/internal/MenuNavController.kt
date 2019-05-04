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

    fun navigate(@IdRes destinationId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        if (mNavGraphId == 0) throw RuntimeException("Not set fragment manager yet!")
        navigate(mNavGraph.findDestination(destinationId), args, navOptions)
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