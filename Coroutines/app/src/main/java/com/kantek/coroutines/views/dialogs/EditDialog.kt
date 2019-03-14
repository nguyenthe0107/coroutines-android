package com.kantek.coroutines.views.dialogs

import android.support.core.base.BaseDialog
import android.support.v4.app.Fragment
import com.kantek.coroutines.R
import kotlinx.android.synthetic.main.dialog_edit.*

class EditDialog(fragment: Fragment) : BaseDialog(fragment) {
    var onOkClickListener: ((Pair<Int, String>) -> Unit)? = null

    init {
        setContentView(R.layout.dialog_edit)
        btnOk.setOnClickListener {
            onOkClickListener?.invoke((edtField.tag as Int) to edtField.text.toString())
            dismiss()
        }
    }

    fun show(field: Int, text: String) {
        edtField.tag = field
        edtField.setText(text)
        super.show()
    }
}
