package android.support.design.widget

import android.os.Bundle
import android.support.annotation.NavigationRes
import android.support.annotation.NonNull
import androidx.navigation.fragment.NavHostFragment

class SupportNavHostFragment : NavHostFragment() {

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
        private const val KEY_GRAPH_ID = "android-support-nav:fragment:graphId"

        @NonNull
        fun create(@NavigationRes graphResId: Int): NavHostFragment {
            var b: Bundle? = null
            if (graphResId != 0) {
                b = Bundle()
                b.putInt(KEY_GRAPH_ID, graphResId)
            }

            val result = SupportNavHostFragment()
            if (b != null) {
                result.arguments = b
            }
            return result
        }
    }
}