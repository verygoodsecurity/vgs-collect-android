package com.verygoodsecurity.demoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.cmp.CMPActivity
import com.verygoodsecurity.demoapp.collect_activity.CollectActivity
import com.verygoodsecurity.demoapp.collect_compose.activity_case.ComposeActivity
import com.verygoodsecurity.demoapp.collect_compose.fragment_case.ComposeFragmentActivity
import com.verygoodsecurity.demoapp.databinding.ActivityStartBinding
import com.verygoodsecurity.demoapp.date_range_activity.DateRangeActivity
import com.verygoodsecurity.demoapp.collect_fragment.VGSCollectFragmentActivity
import com.verygoodsecurity.demoapp.google_pay.GooglePayActivity
import com.verygoodsecurity.demoapp.payopt.PaymentOptimizationActivity
import com.verygoodsecurity.demoapp.tokenization.TokenizationActivity
import com.verygoodsecurity.demoapp.collect_viewpager.ViewPagerActivity

class StartActivity : AppCompatActivity(R.layout.activity_start) {

    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.tiedVaultId.setText(BuildConfig.VAULT_ID)
        binding.tiedPath.setText(BuildConfig.PATH)
        binding.llPayopt.setOnClickListener {
            startActivity(PaymentOptimizationActivity::class.java)
        }
        binding.llTokenizationFlow.setOnClickListener {
            startActivity(TokenizationActivity::class.java)
        }
        binding.llGooglePayActivityFlow.setOnClickListener {
            startActivity(GooglePayActivity::class.java)
        }
        binding.llCollectActivityFlow.setOnClickListener {
            startActivity(CollectActivity::class.java)
        }
        binding.llCollectFragmentFlow.setOnClickListener {
            startActivity(VGSCollectFragmentActivity::class.java)
        }
        binding.llCollectViewPagerFlow.setOnClickListener {
            startActivity(ViewPagerActivity::class.java)
        }
        binding.llDateRangeViewPagerFlow.setOnClickListener {
            startActivity(DateRangeActivity::class.java)
        }
        binding.llComposeFlow.setOnClickListener {
            startActivity(ComposeActivity::class.java)
        }
        binding.llComposeFragmentFlow.setOnClickListener {
            startActivity(ComposeFragmentActivity::class.java)
        }
        binding.llCmpFlow.setOnClickListener {
            startActivity(CMPActivity::class.java)
        }
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

    companion object {

        private const val SANDBOX = "sandbox"
        private const val LIVE = "live"

        const val KEY_BUNDLE_VAULT_ID = "user_vault_id"
        const val KEY_BUNDLE_ENVIRONMENT = "user_env"
        const val KEY_BUNDLE_PATH = "user_path"
    }
}

fun AppCompatActivity.getStringExtra(key: String, defaultValue: String = ""): String {
    return intent.extras?.getString(key) ?: defaultValue
}