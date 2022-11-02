package com.verygoodsecurity.demoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.verygoodsecurity.demoapp.collect_activity.CollectActivity
import com.verygoodsecurity.demoapp.fragment_case.VGSCollectFragmentActivity
import com.verygoodsecurity.demoapp.payopt.PaymentOptimizationActivity
import com.verygoodsecurity.demoapp.tokenization.TokenizationActivity
import com.verygoodsecurity.demoapp.viewpager_case.VGSViewPagerActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity(R.layout.activity_start) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        tiedVaultId?.setText(BuildConfig.VAULT_ID)
        tiedPath?.setText(BuildConfig.PATH)
        llTokenizationFlow?.setOnClickListener {
            startActivity(TokenizationActivity::class.java)
        }
        llCollectActivityFlow?.setOnClickListener {
            startActivity(CollectActivity::class.java)
        }
        llCollectFragmentFlow?.setOnClickListener {
            startActivity(VGSCollectFragmentActivity::class.java)
        }
        llCollectViewPagerFlow?.setOnClickListener {
            startActivity(VGSViewPagerActivity::class.java)
        }
        llPayopt?.setOnClickListener {
            startActivity(PaymentOptimizationActivity::class.java)
        }
    }

    private fun startActivity(activity: Class<out Activity>) {
        startActivity(Intent(this, activity).apply {
            putExtra(KEY_BUNDLE_VAULT_ID, tiedVaultId.text.toString())
            putExtra(KEY_BUNDLE_PATH, tiedPath.text.toString())
            putExtra(KEY_BUNDLE_ENVIRONMENT, getEnvironment())
        })
    }

    private fun getEnvironment() = when (mbGroupEnvironment.checkedButtonId) {
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