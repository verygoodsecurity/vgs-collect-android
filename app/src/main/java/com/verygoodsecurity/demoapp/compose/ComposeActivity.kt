package com.verygoodsecurity.demoapp.compose

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.HTTPMethod
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.widget.VGSTextInputLayout
import com.verygoodsecurity.vgscollect.widget.compose.CardVerificationCodeEditTextWrapper
import com.verygoodsecurity.vgscollect.widget.compose.VGSCardNumberEditTextWrapper

class ComposeActivity : AppCompatActivity(), VgsCollectResponseListener {

    private val path: String by lazy { getStringExtra(StartActivity.KEY_BUNDLE_PATH) }

    private val collect: VGSCollect by lazy {
        VGSCollect(
            this,
            getStringExtra(StartActivity.KEY_BUNDLE_VAULT_ID, ""),
            getStringExtra(StartActivity.KEY_BUNDLE_ENVIRONMENT, "")
        ).also {
            it.addOnResponseListeners(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        VGSCollectLogger.isEnabled = true
        setContent {
            MaterialTheme {
                Content(collect = collect)
            }
        }
    }

    override fun onResponse(response: VGSResponse?) {
        Toast.makeText(
            this,
            when (response) {
                is VGSResponse.SuccessResponse -> "Success"
                is VGSResponse.ErrorResponse -> "Error"
                else -> throw IllegalStateException()
            },
            Toast.LENGTH_SHORT
        ).show()

        Log.d("VGS", "response = ${response.body}")
    }

    @Composable
    private fun Content(collect: VGSCollect?) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val radius = with(LocalDensity.current) { 4.dp.toPx() }
            val paddingVertical = with(LocalDensity.current) { 16.dp.roundToPx() }
            val paddingHorizontal = with(LocalDensity.current) { 8.dp.roundToPx() }

            VGSCardNumberEditTextWrapper(
                collect = collect,
                fieldName = "card.number",
                modifier = Modifier.fillMaxWidth(),
                onViewCreate = { layout, input ->
                    configureTextInputLayout(
                        layout = layout,
                        hint = "Card Number",
                        radius = radius
                    )
                    configureEditText(
                        input = input,
                        paddingVertical = paddingVertical,
                        paddingHorizontal = paddingHorizontal,
                    )
                    input.setDivider(' ')
                    input.setCardBrandIconGravity(Gravity.END)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CardVerificationCodeEditTextWrapper(
                collect = collect,
                fieldName = "card.cvc",
                modifier = Modifier.fillMaxWidth(),
                onViewCreate = { layout, input ->
                    configureTextInputLayout(
                        layout = layout,
                        hint = "CVC",
                        radius = radius
                    )
                    configureEditText(
                        input = input,
                        paddingVertical = paddingVertical,
                        paddingHorizontal = paddingHorizontal,
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedButton(
                modifier = Modifier.align(alignment = Alignment.End),
                contentPadding = PaddingValues(16.dp),
                onClick = { collect?.asyncSubmit(path, HTTPMethod.POST) }
            ) {
                Text(
                    text = "SUBMIT",
                    color = Color(0xff4b5d69)
                )
            }
        }
    }

    private fun configureTextInputLayout(
        layout: VGSTextInputLayout,
        hint: String,
        radius: Float
    ) {
        layout.setHint(hint)
        layout.setHintTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    layout.context,
                    R.color.fiord
                )
            )
        )
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE)
        layout.setBoxCornerRadius(radius, radius, radius, radius)
    }

    private fun configureEditText(
        input: InputFieldView,
        paddingVertical: Int,
        paddingHorizontal: Int
    ) {
        input.setPadding(paddingVertical, paddingHorizontal, paddingVertical, paddingHorizontal)
    }

    @Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
    @Composable
    private fun Preview() {
        MaterialTheme {
            Content(collect = null)
        }
    }
}