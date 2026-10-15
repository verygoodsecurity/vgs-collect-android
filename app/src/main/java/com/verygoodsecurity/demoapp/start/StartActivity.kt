package com.verygoodsecurity.demoapp.start

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.ui.DemoTheme

class StartActivity : AppCompatActivity() {

    companion object {

        const val KEY_BUNDLE_TENANT_ID = "user_tenant_id"
        const val KEY_BUNDLE_ENVIRONMENT = "user_env"
        const val KEY_BUNDLE_PATH = "user_path"
        const val KEY_BUNDLE_ROUTE_ID = "route_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DemoTheme {
                StartScreen(StartItems.all, ::onFlowClicked)
            }
        }
    }

    private fun onFlowClicked(flow: StartItem.Flow, config: StartConfig) {
        startActivity(Intent(this, flow.screen).apply {
            putExtra(KEY_BUNDLE_TENANT_ID, config.tenantId)
            putExtra(KEY_BUNDLE_PATH, config.path)
            putExtra(KEY_BUNDLE_ENVIRONMENT, config.environment.value)
            putExtra(KEY_BUNDLE_ROUTE_ID, flow.routeId)
        })
    }
}
