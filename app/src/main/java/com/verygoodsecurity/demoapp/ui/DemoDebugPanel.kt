package com.verygoodsecurity.demoapp.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.vgscollect.widget.compose.state.core.BaseFieldState
import org.json.JSONArray
import org.json.JSONObject

/** Result of a submit request, as displayed by [DemoDebugPanel]. */
data class DemoResponse(val isSuccess: Boolean, val code: Int? = null, val body: String?)

private enum class DebugTab(@StringRes val label: Int) {
    STATES(R.string.demo_tab_states),
    RESPONSE(R.string.demo_tab_response)
}

/**
 * Tabbed output for a collect demo: a safe JSON summary of the current field [states]
 * (names, lengths and validity only, never values) and the last submit [response].
 */
@Composable
fun DemoDebugPanel(
    states: List<BaseFieldState>,
    response: DemoResponse?,
    modifier: Modifier = Modifier
) {
    var tab by rememberSaveable { mutableStateOf(DebugTab.STATES) }
    val colors = DemoTheme.colors

    Column(modifier, verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
        DemoSegmentedControl(
            options = DebugTab.entries,
            selected = tab,
            label = { stringResource(it.label) },
            onSelect = { tab = it }
        )
        when (tab) {
            DebugTab.STATES -> {
                val invalid = states.count { !it.isValid }
                DemoCodeBlock(states.toDebugJson()) {
                    DemoChip(stringResource(R.string.demo_json), colors.headerSubtitle)
                    if (invalid > 0) {
                        DemoChip(stringResource(R.string.demo_invalid_count, invalid), colors.error, showDot = true)
                    }
                }
            }

            DebugTab.RESPONSE -> DemoCodeBlock(response?.body?.prettyJson() ?: stringResource(R.string.demo_no_response)) {
                DemoChip(stringResource(R.string.demo_json), colors.headerSubtitle)
                if (response != null) {
                    DemoChip(
                        text = response.code?.toString() ?: stringResource(R.string.demo_error),
                        color = if (response.isSuccess) colors.sandbox else colors.error,
                        showDot = true
                    )
                }
            }
        }
    }
}

/** First validation error of this field, or `null` when it is valid. */
fun BaseFieldState.errorMessage(): String? = validationResult.firstOrNull { !it.isValid }?.errorMsg

/** Never includes field values, only metadata that is safe to display. */
private fun List<BaseFieldState>.toDebugJson(): String = JSONObject()
    .put("states", JSONArray(map {
        JSONObject()
            .put("fieldName", it.fieldName)
            .put("contentLength", it.contentLength)
            .put("isValid", it.isValid)
    }))
    .toString(2)

private fun String.prettyJson(): String = runCatching { JSONObject(this).toString(2) }.getOrDefault(this)
