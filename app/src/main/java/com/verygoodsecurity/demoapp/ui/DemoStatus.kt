@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.verygoodsecurity.demoapp.R

/** Dark message bar with a success or error icon, e.g. the outcome of the last action. */
@Composable
fun DemoStatusBar(text: String, isSuccess: Boolean, modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .heightIn(min = DemoTheme.sizes.statusBarHeight)
            .clip(DemoTheme.shapes.medium)
            .background(colors.code)
            .padding(horizontal = DemoTheme.spacing.l, vertical = DemoTheme.spacing.m),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)
    ) {
        Icon(
            painterResource(if (isSuccess) R.drawable.ic_check else R.drawable.ic_error),
            contentDescription = null,
            tint = if (isSuccess) colors.sandbox else colors.error
        )
        Text(text, style = DemoTheme.typography.label, color = colors.onHeader)
    }
}

/** Numbered list item: [number] in an accent circle followed by [text]. */
@Composable
fun DemoStep(number: Int, text: String, modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)
    ) {
        Box(
            Modifier.size(DemoTheme.sizes.stepBadge).clip(CircleShape).background(colors.accentContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), style = DemoTheme.typography.overline, color = colors.accentDark)
        }
        Text(text, Modifier.weight(1f), style = DemoTheme.typography.body, color = colors.ink)
    }
}
