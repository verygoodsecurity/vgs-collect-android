@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verygoodsecurity.demoapp.R

/** Shared look for outlined text fields, including the VGS SDK fields (`colors = ..., shape = ...`). */
object DemoFieldDefaults {

    val shape: CornerBasedShape
        @Composable get() = DemoTheme.shapes.medium

    @Composable
    fun colors(): TextFieldColors = DemoTheme.colors.let {
        TextFieldDefaults.outlinedTextFieldColors(
            backgroundColor = it.surface,
            focusedBorderColor = it.accent,
            unfocusedBorderColor = it.fieldBorder,
            errorBorderColor = it.error,
            cursorColor = it.accent,
            errorCursorColor = it.error,
        )
    }
}

/** Hint text for a field's `placeholder` slot. */
@Composable
fun DemoPlaceholder(text: String) {
    Text(text, style = DemoTheme.typography.body, color = DemoTheme.colors.muted)
}

/** [DemoCard] that stacks form fields vertically with the standard padding and spacing. */
@Composable
fun DemoFormCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    DemoCard(modifier) {
        Column(
            Modifier.padding(DemoTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.l),
            content = content
        )
    }
}

/**
 * Label above a field and an optional error message below it.
 *
 * @param optional appends a muted "Optional" hint to the label.
 * @param error message shown under the field, or `null` when the field is valid.
 */
@Composable
fun DemoField(
    label: String,
    modifier: Modifier = Modifier,
    optional: Boolean = false,
    error: String? = null,
    field: @Composable () -> Unit
) {
    val colors = DemoTheme.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.s)) {
        Text(labelText(label, optional), style = DemoTheme.typography.label, color = colors.ink)
        field()
        if (error != null) {
            Text(error, style = DemoTheme.typography.overline, color = colors.error)
        }
    }
}

@Composable
private fun labelText(label: String, optional: Boolean): AnnotatedString = buildAnnotatedString {
    append(label)
    if (optional) {
        withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = DemoTheme.colors.muted)) {
            append(" · ${stringResource(R.string.demo_optional)}")
        }
    }
}

/** Filled (primary) or outlined (secondary) button with an optional leading [icon]. */
@Composable
fun DemoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    primary: Boolean = true,
    loading: Boolean = false
) {
    val colors = DemoTheme.colors
    val height = Modifier.heightIn(min = DemoTheme.sizes.touchTarget)
    val content: @Composable () -> Unit = {
        if (loading) {
            CircularProgressIndicator(
                Modifier.size(DemoTheme.sizes.progressIndicator),
                color = LocalContentColor.current,
                strokeWidth = DemoTheme.sizes.progressStroke
            )
            Box(Modifier.size(DemoTheme.spacing.s))
        } else if (icon != null) {
            Icon(icon, contentDescription = null, Modifier.size(18.dp))
            Box(Modifier.size(DemoTheme.spacing.s))
        }
        Text(text, style = DemoTheme.typography.button)
    }
    if (primary) {
        Button(
            onClick = onClick,
            modifier = modifier.then(height),
            enabled = !loading,
            shape = DemoTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colors.accent,
                contentColor = Color.White,
                disabledBackgroundColor = colors.accent,
                disabledContentColor = Color.White,
            ),
        ) { content() }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.then(height),
            shape = DemoTheme.shapes.medium,
            border = BorderStroke(1.dp, colors.fieldBorder),
            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = colors.surface, contentColor = colors.ink),
        ) { content() }
    }
}

/** Small pill label, optionally prefixed with a status dot. */
@Composable
fun DemoChip(text: String, color: Color, modifier: Modifier = Modifier, showDot: Boolean = false) {
    Row(
        modifier
            .heightIn(min = DemoTheme.sizes.chipHeight)
            .clip(DemoTheme.shapes.pill)
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = DemoTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.s)
    ) {
        if (showDot) Box(Modifier.size(DemoTheme.sizes.chipDot).clip(CircleShape).background(color))
        Text(text, style = DemoTheme.typography.overline, color = color)
    }
}

/** Label with a trailing switch; the whole row is one touch target. */
@Composable
fun DemoSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Row(
        modifier.fillMaxWidth().heightIn(min = DemoTheme.sizes.touchTarget),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = DemoTheme.typography.body, color = colors.ink)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.accent,
                checkedTrackAlpha = 1f,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = colors.fieldBorder,
                uncheckedTrackAlpha = 1f,
            )
        )
    }
}

/** Read-only select that opens a menu of [options]; [selected] is one of them. */
@Composable
fun DemoDropdown(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = DemoTheme.colors
    var expanded by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .heightIn(min = DemoTheme.sizes.touchTarget)
                .clip(DemoFieldDefaults.shape)
                .background(colors.surface)
                .border(1.5.dp, colors.fieldBorder, DemoFieldDefaults.shape)
                .clickable { expanded = true }
                .padding(horizontal = DemoTheme.spacing.l),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selected, Modifier.weight(1f), style = DemoTheme.typography.field.copy(fontSize = 14.sp), color = colors.ink)
            Icon(painterResource(R.drawable.ic_expand_more), contentDescription = null, tint = colors.muted)
        }
        DropdownMenu(expanded, onDismissRequest = { expanded = false }) {
            // A stored value may no longer be offered (e.g. saved by an older screen); keep it selectable.
            val items = if (selected in options) options else listOf(selected) + options
            items.forEach {
                DropdownMenuItem(onClick = { onSelected(it); expanded = false }) {
                    Text(it, style = DemoTheme.typography.field.copy(fontSize = 14.sp), color = colors.ink)
                }
            }
        }
    }
}

/** Inline error message with an icon. */
@Composable
fun DemoErrorBanner(text: String, modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .clip(DemoTheme.shapes.medium)
            .background(colors.errorContainer)
            .padding(DemoTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)
    ) {
        Icon(painterResource(R.drawable.ic_error), contentDescription = null, tint = colors.error)
        Text(text, style = DemoTheme.typography.label, color = colors.error)
    }
}
