package com.kantek.coroutines.views.fragments

import android.os.Bundle
import android.support.core.annotations.LayoutId
import android.support.core.base.EmptyViewModel
import android.support.design.widget.MenuHostFragment
import androidx.navigation.NavOptions
import com.kantek.coroutines.R
import com.kantek.coroutines.app.AppFragment
import kotlinx.android.synthetic.main.fragment_comment.*

@LayoutId(R.layout.fragment_comment)
class CommentFragment : AppFragment<EmptyViewModel>() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btnBackToPosts.setOnClickListener {
            MenuHostFragment.findNavController(this)!!
                .navigate(R.id.hostPostFragment, navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.hostPostFragment, false)
                    .build())
        }
    }
}