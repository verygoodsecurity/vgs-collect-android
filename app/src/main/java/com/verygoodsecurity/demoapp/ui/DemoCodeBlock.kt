@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

/** Dark, scrollable monospace block for JSON and other machine output. [chips] sits above the [code]. */
@Composable
fun DemoCodeBlock(
    code: String,
    modifier: Modifier = Modifier,
    chips: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(DemoTheme.shapes.large)
            .background(DemoTheme.colors.code)
            .padding(DemoTheme.spacing.l),
        verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.s),
            content = chips
        )
        Text(
            code,
            Modifier
                .heightIn(max = DemoTheme.sizes.codeMaxHeight)
                .verticalScroll(rememberScrollState())
                .horizontalScroll(rememberScrollState()),
            style = DemoTheme.typography.code,
            color = DemoTheme.colors.onCode
        )
    }
}
