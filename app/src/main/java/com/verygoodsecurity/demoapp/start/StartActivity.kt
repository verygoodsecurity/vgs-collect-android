package com.verygoodsecurity.demoapp.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect.compose.CollectComposeActivity
import com.verygoodsecurity.demoapp.collect.views.CollectViewsActivity
import com.verygoodsecurity.demoapp.databinding.ActivityStartBinding
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.payopt.PaymentOptimizationActivity
import com.verygoodsecurity.demoapp.start.adapter.StartFlowAdapter
import com.verygoodsecurity.demoapp.start.decoration.GridSpacingItemDecoration
import com.verygoodsecurity.demoapp.tokenization.v1.TokenizationActivity as TokenizationActivityV1
import com.verygoodsecurity.demoapp.tokenization.v2.TokenizationActivity as TokenizationActivityV2

class StartActivity : AppCompatActivity(R.layout.activity_start) {

    companion object {

        private const val FLOWS_SPAN_COUNT = 2

        private const val SANDBOX = "sandbox"
        private const val LIVE = "live"

        const val KEY_BUNDLE_VAULT_ID = "user_vault_id"
        const val KEY_BUNDLE_ENVIRONMENT = "user_env"
        const val KEY_BUNDLE_PATH = "user_path"
        const val KEY_BUNDLE_ROUTE_ID = "route_id"
    }

    private lateinit var binding: ActivityStartBinding
    private val flowAdapter = StartFlowAdapter(::onFlowItemClicked)

    private val flowItems by lazy {
        listOf(
            StartFlowAdapter.FlowItem(
                id = R.string.start_collect_views_title,
                title = getString(R.string.start_collect_views_title),
                description = getString(R.string.start_collect_views_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_collect_compose_title,
                title = getString(R.string.start_collect_compose_title),
                description = getString(R.string.start_collect_compose_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_payopt_title,
                title = getString(R.string.start_payopt_title),
                description = getString(R.string.start_payopt_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_tokenization_title,
                title = getString(R.string.start_tokenization_title),
                description = getString(R.string.start_tokenization_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_tokenization_v2_title,
                title = getString(R.string.start_tokenization_v2_title),
                description = getString(R.string.start_tokenization_v2_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_cmp_title,
                title = getString(R.string.start_cmp_title),
                description = getString(R.string.start_cmp_description)
            ),
            StartFlowAdapter.FlowItem(
                id = R.string.start_google_pay_title,
                title = getString(R.string.start_google_pay_title),
                description = getString(R.string.start_google_pay_description)
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        binding.tiedVaultId.setText(BuildConfig.VAULT_ID)
        binding.tiedPath.setText(BuildConfig.PATH)

        binding.rvFlows.layoutManager = GridLayoutManager(this, FLOWS_SPAN_COUNT)
        if (binding.rvFlows.itemDecorationCount == 0) {
            binding.rvFlows.addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = FLOWS_SPAN_COUNT,
                    spacing = resources.getDimensionPixelSize(R.dimen.margin_padding_material_micro),
                    includeEdge = false
                )
            )
        }
        binding.rvFlows.adapter = flowAdapter
        flowAdapter.submitList(flowItems)
    }

    private fun onFlowItemClicked(flowId: Int) {
        when (flowId) {
            R.string.start_collect_views_title -> startActivity(CollectViewsActivity::class.java)
            R.string.start_collect_compose_title -> startActivity(CollectComposeActivity::class.java)
            R.string.start_payopt_title -> startActivity(PaymentOptimizationActivity::class.java)
            R.string.start_tokenization_title -> startActivity(
                TokenizationActivityV1::class.java,
                routeId = BuildConfig.TOKENIZATION_ROUTE_ID
            )

            R.string.start_tokenization_v2_title -> startActivity(
                TokenizationActivityV2::class.java,
                routeId = BuildConfig.TOKENIZATION_V2_ROUTE_ID
            )
            R.string.start_cmp_title -> startActivity(CMPActivity::class.java)
            R.string.start_google_pay_title -> startActivity(GooglePayActivity::class.java)
        }
    }

    private fun startActivity(activity: Class<out Activity>, routeId: String? = null) {
        startActivity(Intent(this, activity).apply {
            putExtra(KEY_BUNDLE_VAULT_ID, binding.tiedVaultId.text.toString())
            putExtra(KEY_BUNDLE_PATH, binding.tiedPath.text.toString())
            putExtra(KEY_BUNDLE_ENVIRONMENT, getEnvironment())
            putExtra(KEY_BUNDLE_ROUTE_ID, routeId)
        })
    }

    private fun getEnvironment() = when (binding.mbGroupEnvironment.checkedButtonId) {
        R.id.mbSandbox -> SANDBOX
        R.id.mbLive -> LIVE
        else -> SANDBOX
    }
}