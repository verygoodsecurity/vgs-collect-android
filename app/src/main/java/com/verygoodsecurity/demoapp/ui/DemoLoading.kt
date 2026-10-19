@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.verygoodsecurity.demoapp.R

enum class ProgressStepState { DONE, ACTIVE, PENDING }

/** One line of [DemoProgressCard]. */
data class DemoProgressStep(val title: String, val subtitle: String, val state: ProgressStepState)

/** Card shown while something initializes: a spinner tile, a [title], a [description] and the [steps] so far. */
@Composable
fun DemoProgressCard(
    title: String,
    subtitle: String,
    description: String,
    steps: List<DemoProgressStep>,
    modifier: Modifier = Modifier
) {
    val colors = DemoTheme.colors
    DemoCard(modifier, elevation = DemoTheme.sizes.elevationRaised) {
        Column(Modifier.padding(DemoTheme.spacing.l), verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.l)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
                Box(
                    Modifier.size(DemoTheme.sizes.iconTile).clip(DemoTheme.shapes.medium).background(colors.iconBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Spinner(Color.White, DemoTheme.sizes.progressIndicator)
                }
                Column(verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.xxs)) {
                    Text(title, style = DemoTheme.typography.title.copy(fontSize = 16.sp), color = colors.ink)
                    Text(subtitle, style = DemoTheme.typography.overline, color = colors.muted)
                }
            }
            Text(description, style = DemoTheme.typography.caption.copy(fontSize = 13.sp), color = colors.muted)
            Column(verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.sm)) {
                steps.forEach { ProgressStep(it) }
            }
        }
    }
}

@Composable
private fun ProgressStep(step: DemoProgressStep) {
    val colors = DemoTheme.colors
    val pending = step.state == ProgressStepState.PENDING
    Row(
        Modifier.heightIn(min = DemoTheme.sizes.progressStepMinHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)
    ) {
        val (background, content) = when (step.state) {
            ProgressStepState.DONE -> colors.successContainer to colors.success
            ProgressStepState.ACTIVE -> colors.accentContainer to colors.accent
            ProgressStepState.PENDING -> colors.skeleton to colors.fieldBorder
        }
        Box(
            Modifier.size(DemoTheme.sizes.progressStepBadge).clip(CircleShape).background(background),
            contentAlignment = Alignment.Center
        ) {
            when (step.state) {
                ProgressStepState.DONE -> Icon(
                    painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    Modifier.size(DemoTheme.sizes.progressStepIcon),
                    tint = content
                )

                ProgressStepState.ACTIVE -> Spinner(content, DemoTheme.sizes.progressStepIcon)
                ProgressStepState.PENDING -> Box(Modifier.size(DemoTheme.spacing.s).clip(CircleShape).background(content))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                step.title,
                style = DemoTheme.typography.label,
                color = if (pending) colors.subtle else colors.ink
            )
            Text(
                step.subtitle,
                style = DemoTheme.typography.caption,
                color = if (pending) colors.subtle else colors.muted
            )
        }
    }
}

@Composable
private fun Spinner(color: Color, size: Dp) {
    CircularProgressIndicator(Modifier.size(size), color = color, strokeWidth = DemoTheme.sizes.progressStroke)
}

/** Greyed-out placeholder of a two-row form with a button, shown where the real form will appear. */
@Composable
fun DemoFormSkeleton(modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Surface(
        modifier.fillMaxWidth().alpha(0.8f),
        shape = DemoTheme.shapes.large,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.segment)
    ) {
        Column(Modifier.padding(DemoTheme.spacing.l), verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
            SkeletonField(44.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(DemoTheme.spacing.m)) {
                SkeletonField(36.dp, Modifier.weight(1f))
                SkeletonField(52.dp, Modifier.weight(1f))
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(DemoTheme.sizes.skeletonButtonHeight)
                    .clip(DemoTheme.shapes.medium)
                    .background(colors.segment)
            )
        }
    }
}

@Composable
private fun SkeletonField(labelWidth: Dp, modifier: Modifier = Modifier) {
    val colors = DemoTheme.colors
    Column(modifier, verticalArrangement = Arrangement.spacedBy(DemoTheme.spacing.s)) {
        Box(
            Modifier
                .width(labelWidth)
                .height(DemoTheme.sizes.skeletonLabelHeight)
                .clip(DemoTheme.shapes.small)
                .background(colors.segment)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(DemoTheme.sizes.skeletonFieldHeight)
                .clip(DemoTheme.shapes.medium)
                .background(colors.skeleton)
                .border(1.5.dp, colors.segment, DemoTheme.shapes.medium)
        )
    }
}
