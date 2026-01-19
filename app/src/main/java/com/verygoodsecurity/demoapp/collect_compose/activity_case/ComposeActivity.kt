package com.verygoodsecurity.demoapp.collect_compose.activity_case

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.verygoodsecurity.demoapp.StartActivity
import com.verygoodsecurity.demoapp.collect_compose.components.BaseCollect
import com.verygoodsecurity.demoapp.getStringExtra
import com.verygoodsecurity.vgscollect.VGSCollectLogger
import com.verygoodsecurity.vgscollect.core.VGSCollect
import com.verygoodsecurity.vgscollect.core.VgsCollectResponseListener
import com.verygoodsecurity.vgscollect.core.model.network.VGSResponse

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
        enableEdgeToEdge()
        VGSCollectLogger.logLevel = VGSCollectLogger.Level.DEBUG
        VGSCollectLogger.isEnabled = true
        setContent {
            Content(
                collect = collect,
                path = path
            )
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
}

@Composable
private fun Content(
    collect: VGSCollect?,
    path: String
) {
    MaterialTheme {
        BaseCollect(
            collect = collect,
            path = path
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun Preview() {
    Content(
        collect = null,
        path = ""
    )
}
