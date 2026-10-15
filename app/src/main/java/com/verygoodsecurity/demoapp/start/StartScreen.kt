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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.ui.CollapsingHeaderState
import com.verygoodsecurity.demoapp.ui.DemoCard
import com.verygoodsecurity.demoapp.ui.DemoColors
import com.verygoodsecurity.demoapp.ui.DemoSegmentedControl
import com.verygoodsecurity.demoapp.ui.DemoTextField
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

    Column(
        Modifier
            .fillMaxSize()
            .background(DemoColors.Background)
            .imePadding()
            .nestedScroll(headerState.nestedScrollConnection)
    ) {
        StartHeader(environment, headerState)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DemoCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            stringResource(R.string.start_activity_environment_title),
                            color = DemoColors.Ink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
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
    Column(
        Modifier
            .fillMaxWidth()
            .background(DemoColors.HeaderBrush)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.ic_vgs_logo_white), contentDescription = null, Modifier.size(32.dp, 36.dp))
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    stringResource(R.string.start_header_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    stringResource(R.string.start_header_subtitle).uppercase(),
                    color = DemoColors.HeaderSubtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            EnvironmentChip(environment)
        }
        Box(Modifier.collapsible(state)) {
            Text(
                stringResource(R.string.start_header_hint),
                modifier = Modifier.padding(top = 14.dp, bottom = 10.dp),
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun EnvironmentChip(environment: StartEnvironment) {
    Row(
        Modifier
            .heightIn(min = 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(DemoColors.Sandbox.copy(alpha = 0.16f))
            .padding(horizontal = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(DemoColors.Sandbox))
        Text(
            stringResource(environment.label),
            color = DemoColors.Sandbox,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionHeader(item: StartItem.Header) {
    Text(
        text = stringResource(item.title).uppercase(),
        modifier = Modifier.padding(top = 10.dp),
        color = DemoColors.Section,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun FlowCard(item: StartItem.Flow, onClick: () -> Unit) {
    DemoCard(onClick = onClick) {
        Row(
            Modifier.heightIn(min = 84.dp).padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(10.dp)).background(DemoColors.IconBrush),
                contentAlignment = Alignment.Center
            ) {
                Icon(painterResource(item.icon), contentDescription = null, tint = Color.White)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    stringResource(item.title),
                    color = DemoColors.Ink,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    stringResource(item.description),
                    color = DemoColors.Muted,
                    fontSize = 12.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.start_activity_start_btn_title),
                    color = DemoColors.Accent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(painterResource(R.drawable.ic_chevron_end), contentDescription = null, tint = Color.Unspecified)
            }
        }
    }
}
