package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

/** White [icon] on the brand gradient, used as a section marker. */
@Composable
fun DemoIconTile(icon: Painter, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(DemoTheme.sizes.sectionTile)
            .clip(DemoTheme.shapes.small)
            .background(DemoTheme.colors.iconBrush),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, Modifier.size(DemoTheme.sizes.sectionTileIcon), tint = Color.White)
    }
}
