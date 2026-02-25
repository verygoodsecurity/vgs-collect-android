package com.verygoodsecurity.demoapp.start

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.verygoodsecurity.demoapp.BuildConfig
import com.verygoodsecurity.demoapp.R
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect.compose.CollectComposeActivity
import com.verygoodsecurity.demoapp.collect.views.CollectViewsActivity
import com.verygoodsecurity.demoapp.databinding.ActivityStartBinding
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.payopt.PaymentOptimizationActivity
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
    }

    private lateinit var binding: ActivityStartBinding

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
        binding.rvFlows.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = FLOWS_SPAN_COUNT,
                spacing = getResources().getDimensionPixelSize(R.dimen.margin_padding_material_micro),
                includeEdge = false
            )
        )
        binding.rvFlows.adapter = StartFlowAdapter(
            data = listOf(
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_collect_views_title),
                    description = getString(R.string.start_collect_views_description),
                    onClick = {
                        startActivity(activity = CollectViewsActivity::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_collect_compose_title),
                    description = getString(R.string.start_collect_compose_description),
                    onClick = {
                        startActivity(activity = CollectComposeActivity::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_payopt_title),
                    description = getString(R.string.start_payopt_description),
                    onClick = {
                        startActivity(activity = PaymentOptimizationActivity::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_tokenization_title),
                    description = getString(R.string.start_tokenization_description),
                    onClick = {
                        startActivity(activity = TokenizationActivityV1::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_tokenization_v2_title),
                    description = getString(R.string.start_tokenization_v2_description),
                    onClick = {
                        startActivity(activity = TokenizationActivityV2::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_cmp_title),
                    description = getString(R.string.start_cmp_description),
                    onClick = {
                        startActivity(activity = CMPActivity::class.java)
                    }
                ),
                StartFlowAdapter.FlowItem(
                    title = getString(R.string.start_google_pay_title),
                    description = getString(R.string.start_google_pay_description),
                    onClick = {
                        startActivity(activity = GooglePayActivity::class.java)
                    }
                ),
            )
        )
    }

    private fun startActivity(activity: Class<out Activity>) {
        startActivity(Intent(this, activity).apply {
            putExtra(KEY_BUNDLE_VAULT_ID, binding.tiedVaultId.text.toString())
            putExtra(KEY_BUNDLE_PATH, binding.tiedPath.text.toString())
            putExtra(KEY_BUNDLE_ENVIRONMENT, getEnvironment())
        })
    }

    private fun getEnvironment() = when (binding.mbGroupEnvironment.checkedButtonId) {
        R.id.mbSandbox -> SANDBOX
        R.id.mbLive -> LIVE
        else -> throw IllegalArgumentException("Not implemented")
    }
}

class StartFlowAdapter(
    private val data: List<FlowItem>
) : RecyclerView.Adapter<StartFlowAdapter.ViewHolder>() {

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        private val mTvTitle: MaterialTextView = view.findViewById(R.id.mTvTitle)
        private val mTvDescription: MaterialTextView = view.findViewById(R.id.mTvDescription)

        fun bind(item: FlowItem) {
            mTvTitle.text = item.title
            mTvDescription.text = item.description
            view.setOnClickListener { item.onClick() }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.start_flow_card_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun getItemCount() = data.size

    data class FlowItem(
        val title: String,
        val description: String,
        val onClick: () -> Unit
    )
}

fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
    return intent.extras?.getString(key) ?: defaultValue
}