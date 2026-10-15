@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.ui.CollapsingHeaderState
import com.verygoodsecurity.demoapp.ui.DemoCard
import com.verygoodsecurity.demoapp.ui.DemoChip
import com.verygoodsecurity.demoapp.ui.DemoSegmentedControl
import com.verygoodsecurity.demoapp.ui.DemoTextField
import com.verygoodsecurity.demoapp.ui.DemoTheme
import com.verygoodsecurity.demoapp.ui.collapsible
import com.verygoodsecurity.demoapp.ui.rememberCollapsingHeaderState

const val TAG_TENANT_ID = "tenantId"
const val TAG_PATH = "path"

@Composable
fun StartScreen(
    items: List<StartItem>,
    onFlowClick: (StartItem.Flow, StartConfig) -> Unit
) {
    var tenantId by rememberSaveable { mutableStateOf(StartConfig().tenantId) }
    var path by rememberSaveable { mutableStateOf(StartConfig().path) }
    var environment by rememberSaveable { mutableStateOf(StartEnvironment.SANDBOX) }

    val headerState = rememberCollapsingHeaderState()
    val spacing = DemoTheme.spacing

    Column(
        Modifier
            .fillMaxSize()
            .background(DemoTheme.colors.background)
            .imePadding()
            .nestedScroll(headerState.nestedScrollConnection)
    ) {
        StartHeader(environment, headerState)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(spacing.xl),
            verticalArrangement = Arrangement.spacedBy(spacing.m)
        ) {
            DemoCard {
                Column(Modifier.padding(spacing.l), verticalArrangement = Arrangement.spacedBy(spacing.l)) {
                    DemoTextField(
                        label = stringResource(R.string.start_activity_tenant_id_hint),
                        value = tenantId,
                        onValueChange = { tenantId = it },
                        modifier = Modifier.testTag(TAG_TENANT_ID)
                    )
                    DemoTextField(
                        label = stringResource(R.string.start_activity_path_hint),
                        value = path,
                        onValueChange = { path = it },
                        modifier = Modifier.testTag(TAG_PATH)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                        Text(
                            stringResource(R.string.start_activity_environment_title),
                            style = DemoTheme.typography.label,
                            color = DemoTheme.colors.ink
                        )
                        DemoSegmentedControl(
                            options = StartEnvironment.entries,
                            selected = environment,
                            label = { stringResource(it.label) },
                            onSelect = { environment = it }
                        )
                    }
                }
            }
            items.forEach { item ->
                when (item) {
                    is StartItem.Header -> SectionHeader(item)
                    is StartItem.Flow -> FlowCard(item) {
                        onFlowClick(item, StartConfig(tenantId, path, environment))
                    }
                }
            }
        }
    }
}

@Composable
private fun StartHeader(environment: StartEnvironment, state: CollapsingHeaderState) {
    val colors = DemoTheme.colors
    val spacing = DemoTheme.spacing
    Column(
        Modifier
            .fillMaxWidth()
            .background(colors.headerBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = spacing.xl, vertical = spacing.l)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.ic_vgs_logo_white),
                contentDescription = null,
                Modifier.size(DemoTheme.sizes.logo)
            )
            Column(Modifier.weight(1f).padding(horizontal = spacing.m)) {
                Text(
                    stringResource(R.string.start_header_title),
                    style = DemoTheme.typography.headline,
                    color = colors.onHeader
                )
                Text(
                    stringResource(R.string.start_header_subtitle).uppercase(),
                    style = DemoTheme.typography.overline,
                    color = colors.headerSubtitle
                )
            }
            DemoChip(stringResource(environment.label), colors.sandbox, showDot = true)
        }
        Box(Modifier.collapsible(state)) {
            Text(
                stringResource(R.string.start_header_hint),
                modifier = Modifier.padding(top = spacing.l, bottom = spacing.m),
                style = DemoTheme.typography.body,
                color = colors.onHeader.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
private fun SectionHeader(item: StartItem.Header) {
    Text(
        text = stringResource(item.title).uppercase(),
        modifier = Modifier.padding(top = DemoTheme.spacing.m),
        style = DemoTheme.typography.overline,
        color = DemoTheme.colors.section
    )
}

@Composable
private fun FlowCard(item: StartItem.Flow, onClick: () -> Unit) {
    val colors = DemoTheme.colors
    val spacing = DemoTheme.spacing
    DemoCard(onClick = onClick) {
        Row(
            Modifier.heightIn(min = DemoTheme.sizes.cardMinHeight).padding(spacing.l),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.m)
        ) {
            Box(
                Modifier.size(DemoTheme.sizes.iconTile).clip(DemoTheme.shapes.small).background(colors.iconBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(item.icon), contentDescription = null, tint = Color.White)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    stringResource(item.title),
                    style = DemoTheme.typography.title,
                    color = colors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    stringResource(item.description),
                    style = DemoTheme.typography.caption,
                    color = colors.muted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.start_activity_start_btn_title),
                    style = DemoTheme.typography.label,
                    color = colors.accent
                )
                Icon(painterResource(R.drawable.ic_chevron_end), contentDescription = null, tint = Color.Unspecified)
            }
        }
    }
}
