package com.verygoodsecurity.demoapp.google_pay

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.verygoodsecurity.demoapp.R

// Dimensions and colors follow the Google Pay button guidelines, so they are not demo theme tokens.
private val ButtonHeight = 48.dp
private val ButtonShape = RoundedCornerShape(percent = 50)
private val ButtonBorder = Color(0xFF747775)

/** Black "Pay with Google Pay" button; dimmed and inactive while [enabled] is false. */
@Composable
fun GooglePayButton(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Image(
        painter = painterResource(R.drawable.pay_with_googlepay_button_content),
        contentDescription = stringResource(R.string.pay_with_googlepay_button_content_description),
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(ButtonShape)
            .background(Color.Black)
            .border(1.dp, ButtonBorder, ButtonShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 11.dp)
    )
}
