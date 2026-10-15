@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.R

/**
 * Standard demo screen: gradient top bar with a back button, followed by a scrollable, inset-aware
 * and keyboard-aware column of [content].
 *
 * @param actions icon buttons shown at the end of the top bar, see [DemoIconButton].
 */
@Composable
fun DemoScreen(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(DemoTheme.colors.background)
            .imePadding()
    ) {
        DemoTopBar(title, onBack, actions = actions)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(DemoTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.l),
            content = content
        )
    }
}

@Composable
fun DemoTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(DemoTheme.colors.headerBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = DemoTheme.spacing.s, vertical = DemoTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DemoIconButton(painterResource(R.drawable.ic_arrow_back), stringResource(R.string.demo_back), onBack)
        Text(
            title,
            Modifier.weight(1f).padding(horizontal = DemoTheme.spacing.xs),
            style = DemoTheme.typography.heading,
            color = DemoTheme.colors.onHeader
        )
        actions()
    }
}

/** Icon-only button with a minimum touch target, tinted for use on the gradient top bar. */
@Composable
fun DemoIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(onClick, modifier.size(DemoTheme.sizes.touchTarget)) {
        Icon(icon, contentDescription, tint = DemoTheme.colors.onHeader)
    }
}
