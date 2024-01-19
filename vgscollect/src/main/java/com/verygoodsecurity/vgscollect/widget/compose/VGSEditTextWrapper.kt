package com.verygoodsecurity.vgscollect.widget.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout

@Composable
fun VGSEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, VGSEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, VGSEditText) -> Unit)? = null,
) {
    var input by remember { mutableStateOf<VGSEditText?>(null) }

    DisposableEffect(Unit) {

        onDispose {
            collect?.unbindView(input)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            val layout = VGSTextInputLayout(it)
            val view = VGSEditText(layout.context).also { view -> input = view }
            view.setFieldName(fieldName)
            onViewCreate?.invoke(layout, view)
            layout.addView(view)
            collect?.bindView(view)
            layout
        },
        update = { layout -> input?.let { onViewUpdate?.invoke(layout, it) } }
    )
}
