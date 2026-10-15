@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun DemoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = DemoTheme.shapes.large,
        color = DemoTheme.colors.surface,
        border = BorderStroke(1.dp, DemoTheme.colors.border),
        elevation = 1.dp,
    ) {
        Box(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier) { content() }
    }
}

/** Labelled monospace text field. [modifier] is applied to the field itself. */
@Composable
fun DemoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = DemoTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.s)) {
        Text(label, style = DemoTheme.typography.label, color = colors.ink)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = DemoTheme.typography.field.copy(color = colors.ink),
            shape = DemoTheme.shapes.medium,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                backgroundColor = colors.surface,
                focusedBorderColor = colors.accent,
                unfocusedBorderColor = colors.fieldBorder,
                cursorColor = colors.accent,
            ),
        )
    }
}

@Composable
fun <T> DemoSegmentedControl(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = DemoTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(DemoTheme.shapes.medium)
            .background(colors.segment)
            .padding(DemoTheme.spacing.xs)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.xxs)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(DemoTheme.sizes.controlHeight)
                    .clip(DemoTheme.shapes.small)
                    .background(if (isSelected) colors.surface else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(option) }),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label(option),
                    style = DemoTheme.typography.label,
                    color = if (isSelected) colors.ink else colors.muted
                )
            }
        }
    }
}
