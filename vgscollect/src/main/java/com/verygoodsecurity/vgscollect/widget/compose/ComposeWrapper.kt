package com.verygoodsecurity.vgscollect.widget.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.CardVerificationCodeEditText
import com.verygoodsecurity.vgscollect.widget.ExpirationDateEditText
import com.verygoodsecurity.vgscollect.widget.PersonNameEditText
import com.verygoodsecurity.vgscollect.widget.RangeDateEditText
import com.verygoodsecurity.vgscollect.widget.SSNEditText
import com.verygoodsecurity.vgscollect.widget.VGSCardNumberEditText
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout

/**
 *  [VGSEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [VGSEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun VGSEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, VGSEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, VGSEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { VGSEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [VGSCardNumberEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [VGSCardNumberEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun VGSCardNumberEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, VGSCardNumberEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, VGSCardNumberEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { VGSCardNumberEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [SSNEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [SSNEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun SSNEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, SSNEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, SSNEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { SSNEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [RangeDateEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [RangeDateEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun RangeDateEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, RangeDateEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, RangeDateEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { RangeDateEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [PersonNameEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [PersonNameEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun PersonNameEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, PersonNameEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, PersonNameEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { PersonNameEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [ExpirationDateEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [ExpirationDateEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun ExpirationDateEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, ExpirationDateEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, ExpirationDateEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { ExpirationDateEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

/**
 *  [ExpirationDateEditText] compose wrapper.
 *
 *  @param collect - [VGSCollect] instance.
 *  @param fieldName - field-name in JSON path in inbound route filters.
 *  @param modifier - [AndroidView] wrapper compose modifier.
 *  @param onViewCreate - A callback to be invoked when [VGSTextInputLayout] and [ExpirationDateEditText] view is created.
 *  @param onViewUpdate - A callback to be invoked after the view is inflated and upon recomposition to update the information and state of the view.
 */
@Composable
fun CardVerificationCodeEditTextWrapper(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    onViewCreate: ((VGSTextInputLayout, CardVerificationCodeEditText) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, CardVerificationCodeEditText) -> Unit)? = null,
) {
    Root(
        collect = collect,
        fieldName = fieldName,
        modifier = modifier,
        createView = { CardVerificationCodeEditText(it) },
        onViewCreate = onViewCreate,
        onViewUpdate = onViewUpdate
    )
}

@Composable
internal fun <T : InputFieldView> Root(
    collect: VGSCollect?,
    fieldName: String,
    modifier: Modifier = Modifier,
    createView: (Context) -> T,
    onViewCreate: ((VGSTextInputLayout, T) -> Unit)? = null,
    onViewUpdate: ((VGSTextInputLayout, T) -> Unit)? = null,
) {
    var input by remember { mutableStateOf<T?>(null) }

    DisposableEffect(Unit) {

        onDispose {
            collect?.unbindView(input)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            val layout = VGSTextInputLayout(it)
            val view = createView(layout.context).also { view -> input = view }
            view.setFieldName(fieldName)
            onViewCreate?.invoke(layout, view)
            layout.addView(view)
            collect?.bindView(view)
            layout
        },
        update = { layout -> input?.let { onViewUpdate?.invoke(layout, it) } }
    )
}
